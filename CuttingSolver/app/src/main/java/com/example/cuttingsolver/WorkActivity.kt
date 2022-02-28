package com.example.cuttingsolver

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.AsyncTask
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cuttingsolver.cuttingAlgorithm.*
import kotlinx.android.synthetic.main.activity_work.*
import kotlinx.android.synthetic.main.item.view.*
import kotlinx.android.synthetic.main.item_res.view.*
import kotlin.math.roundToInt
import android.R.attr.data
import android.annotation.TargetApi
import android.content.pm.PackageManager
import android.os.Environment
import androidx.core.app.NotificationCompat.getExtras
import android.os.Environment.getExternalStorageDirectory
import android.provider.MediaStore
import com.itextpdf.text.Image
import com.itextpdf.text.pdf.PdfWriter
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class WorkActivity : AppCompatActivity() {

    var sql = DbSql(this)
    var panelArray=ArrayList<dataStock>()
    var stockArray=ArrayList<dataStock>()
    var panelData = ArrayList<Panel>()
    var shapeData = ArrayList<Shape>()
    var ch:Boolean? = null
    var chromosome = ArrayList<Chromosome>()
    var img = ArrayList<Bitmap>()
    private val pdfCode = 33
    private val imgCode = 22

    @SuppressLint("WrongConstant")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_work)

        setSupportActionBar(toolbar)
        supportActionBar?.title="Cutting Algorithm"

//        initData()
        read()

        panel_list.layoutManager=LinearLayoutManager(this,LinearLayout.VERTICAL,false)

        stock_list.layoutManager=LinearLayoutManager(this,LinearLayout.VERTICAL,false)

        result_list.layoutManager = LinearLayoutManager(this,LinearLayout.VERTICAL,false)

        stock_show.setOnClickListener {
            if (stock_content.visibility==View.GONE){
                work_img_stock.setImageResource(R.drawable.down_arrow)
                stock_content.visibility=View.VISIBLE
            }else{
                work_img_stock.setImageResource(R.drawable.right_arrow)
                stock_content.visibility=View.GONE
            }
        }

        panel_show.setOnClickListener {
            if (panel_content.visibility==View.GONE){
                work_img_panel.setImageResource(R.drawable.down_arrow)
                panel_content.visibility=View.VISIBLE
            }else{
                work_img_panel.setImageResource(R.drawable.right_arrow)
                panel_content.visibility=View.GONE
            }
        }


    }

    private fun read(){
        try {
            panelArray.clear()
            stockArray.clear()
            val listData:ArrayList<Material> = sql.getData()
            for (item in listData){
                if (item.type == "panel"){
                    panelArray.add(dataStock(item.wid,item.high,item.qty))
                }else{
                    stockArray.add(dataStock(item.wid,item.high,item.qty))
                }

            }
            while (panelArray.size < 5){
                panelArray.add(dataStock("","",""))
            }

            while (stockArray.size < 5){
                stockArray.add(dataStock("","",""))
            }

            val arrayAdapter=MyAdapterPanel()
            panel_list.adapter=arrayAdapter

            val arrayAdapter1=MyAdapterStock()
            stock_list.adapter=arrayAdapter1

//            Toast.makeText(this,listData.size.toString(),Toast.LENGTH_LONG).show()
//            Toast.makeText(this,stockArray.toString(),Toast.LENGTH_LONG).show()
//            Toast.makeText(this,panelArray.toString(),Toast.LENGTH_LONG).show()
        }catch (e:Exception){
            Toast.makeText(this,e.message.toString(),Toast.LENGTH_LONG).show()
        }

    }

    private fun initData(){


        panelArray.add(dataStock("","",""))
        panelArray.add(dataStock("","",""))
        panelArray.add(dataStock("","",""))
        panelArray.add(dataStock("","",""))
        panelArray.add(dataStock("","",""))

        stockArray.add(dataStock("","",""))
        stockArray.add(dataStock("","",""))
        stockArray.add(dataStock("","",""))
        stockArray.add(dataStock("","",""))
        stockArray.add(dataStock("","",""))

        val arrayAdapter=MyAdapterPanel()
        panel_list.adapter=arrayAdapter

        val arrayAdapter1=MyAdapterStock()
        stock_list.adapter=arrayAdapter1
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.my_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    @TargetApi(Build.VERSION_CODES.M)
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == R.id.menu_expPDF){
            if(ch == true) {
                img.clear()
                for (p in chromosome[0].pnl!!) {
                    if (p.item!!.size > 0) {
                        img.add(showP(p))
                    }
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    checkPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE,pdfCode)
                }
            }else{
                Toast.makeText(this,"No Solution Exist",Toast.LENGTH_LONG).show()
            }
        }

        if(item.itemId == R.id.menu_expImg){
            if(ch == true) {
                img.clear()
                for (p in chromosome[0].pnl!!) {
                    if (p.item!!.size > 0) {
                        img.add(showP(p))
                    }
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    checkPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE,imgCode)
                }
            }else{
                Toast.makeText(this,"No Solution Exist",Toast.LENGTH_LONG).show()
            }
        }

        if (item.itemId == R.id.menu_save){
            saveData()
        }

        if (item.itemId == R.id.menu_clear){
            panelArray.clear()
            stockArray.clear()

            initData()
            sql.deleteData()

        }

        return true
    }

    private fun saveData(){
        val dataArray = ArrayList<Material>()

        val panelTempData = ArrayList<dataStock>()
        val shapeTempData = ArrayList<dataStock>()
        for (i in panelArray){
            val w = i.wid
            val l = i.len
            val q = i.qty
            if (l != "" && w != "" && q != ""){
                panelTempData.add(dataStock(w,l,q))
            }
        }
        for (i in stockArray){
            val w = i.wid
            val l = i.len
            val q = i.qty
            if (l != "" && w != "" && q != ""){
                shapeTempData.add(dataStock(w,l,q))
            }
        }
        while (shapeTempData.size < 5){
            shapeTempData.add(dataStock("","",""))
        }
        while (panelTempData.size < 5){
            panelTempData.add(dataStock("","",""))
        }

        for (element in panelTempData){
            dataArray.add(Material(1,element.wid,element.len,element.qty,"panel"))
        }
        for (element in shapeTempData){
            dataArray.add(Material(1,element.wid,element.len,element.qty,"shape"))
        }
        try{
            var res = true
//            Toast.makeText(this,"panel "+panelTempData.toString(),Toast.LENGTH_LONG).show()
//            Toast.makeText(this,"shape "+shapeTempData.toString(),Toast.LENGTH_LONG).show()
            sql.deleteData()
//            Toast.makeText(this,"all "+dataArray.size.toString(),Toast.LENGTH_LONG).show()
            for ( x in dataArray){
                res = sql.insertData(x.wid,x.high,x.qty,x.type)
            }
            if (res){
                Toast.makeText(this,"Done Successfully",Toast.LENGTH_LONG).show()
            }else{
                Toast.makeText(this,"Failed Store in Data Base",Toast.LENGTH_LONG).show()
            }
        }catch (e:Exception){
            Toast.makeText(this,e.message.toString(),Toast.LENGTH_LONG).show()
        }
    }

    private fun extractData(){
        val panelTempData = ArrayList<dataStock>()
        val shapeTempData = ArrayList<dataStock>()
        for (item in panelArray){
            val w = item.wid
            val l = item.len
            val q = item.qty
            if (l != "" && w != "" && q != ""){
                panelTempData.add(dataStock(w,l,q))
            }
        }

        panelData.clear()
        for (item in panelTempData){
            for (i in 0 until item.qty.toInt()){
                panelData.add(Panel(item.wid.toInt(),item.len.toInt()))
            }
        }
        shapeData.clear()
        for (item in stockArray){
            val w = item.wid
            val l = item.len
            val q = item.qty
            if (l != "" && w != "" && q != ""){
                shapeTempData.add(dataStock(w,l,q))
            }
        }

        for (item in shapeTempData){
            for (i in 0 until item.qty.toInt()){
                shapeData.add(Shape(item.wid.toInt(),item.len.toInt()))
            }
        }

    }

    fun workClick(view: View){
        img.clear()
        val c = Chromosome()
        val p = ArrayList<Panel>()
        p.add(Panel(100,100))
        c.putPanel(p)
        val arrayAdapter1 = CustomAdapter(c.pnl!!)  // chromosome[0].pnl!!
        result_list.adapter = arrayAdapter1

        extractData()
        if (panelData.size > 0){
            if (shapeData.size > 0){


                val tt = MyTask()
                tt.execute()

                while (ch==null){  }

                if (ch as Boolean){
                    Toast.makeText(applicationContext,"Done !",Toast.LENGTH_LONG).show()
                    val arrayAdapter = CustomAdapter(chromosome[0].pnl!!)
                    result_list.adapter = arrayAdapter
                }else{
                    Toast.makeText(applicationContext,"Error : Can not cutting , Panel isn't ",Toast.LENGTH_LONG).show()
                }



            }else{
                Toast.makeText(this,"Please , Enter complete 1 stock data",Toast.LENGTH_LONG).show()
            }
        }else{
            Toast.makeText(this,"Please , Enter complete 1 panel data",Toast.LENGTH_LONG).show()
        }


    }

    inner class MyAdapterPanel : RecyclerView.Adapter<MyAdapterPanel.ViewHolder>(),TextWatcher{
        override fun afterTextChanged(s: Editable?) {

        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {

            holder.wid.addTextChangedListener(object :TextWatcher{
                override fun afterTextChanged(s: Editable?) {

                }

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                   if (s?.length!! >0){
                       holder.img.visibility=View.VISIBLE
                   }

                    if(holder.wid.text.isEmpty()&&holder.len.text.isEmpty()&&holder.qty.text.isEmpty()){
                        holder.img.visibility=View.GONE
                    }
                    if(panelArray.size-1==position){
                        if(panelArray.size<10) {
                            panelArray.add(dataStock("", "", ""))
                        }
                    }
                    panelArray[position].wid = holder.wid.text.toString()
//                    Toast.makeText(holder.len.context,"panel position : $position",Toast.LENGTH_LONG).show()
                }

            })

            holder.len.addTextChangedListener(object :TextWatcher{
                override fun afterTextChanged(s: Editable?) {

                }

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (s?.length!! >0){
                        holder.img.visibility=View.VISIBLE
                    }
                    if(holder.wid.text.isEmpty()&&holder.len.text.isEmpty()&&holder.qty.text.isEmpty()){
                        holder.img.visibility=View.GONE
                    }
                    if(panelArray.size-1==position){
                        if(panelArray.size<10) {
                            panelArray.add(dataStock("", "", ""))
                        }
                    }
                    panelArray[position].len = holder.len.text.toString()
//                    Toast.makeText(holder.len.context,"panel position : $position",Toast.LENGTH_LONG).show()
                }

            })

            holder.qty.addTextChangedListener(object :TextWatcher{
                override fun afterTextChanged(s: Editable?) {

                }

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (s?.length!! >0){
                        holder.img.visibility=View.VISIBLE
                    }

                    if(holder.wid.text.isEmpty()&&holder.len.text.isEmpty()&&holder.qty.text.isEmpty()){
                        holder.img.visibility=View.GONE
                    }
                    if(panelArray.size-1==position){
                        if(panelArray.size<10) {
                            panelArray.add(dataStock("", "", ""))
                        }
                    }
                    panelArray[position].qty = holder.qty.text.toString()
//                    Toast.makeText(holder.len.context,"panel position : $position",Toast.LENGTH_LONG).show()
                }

            })

            holder.clear.setOnClickListener {
                panelArray[position].wid = ""
                panelArray[position].len = ""
                panelArray[position].qty = ""
                holder.wid.text.clear()
                holder.len.text.clear()
                holder.qty.text.clear()
                holder.img.visibility=View.GONE
            }

            if (panelArray.size > 0){
                holder.wid.setText(panelArray[position].wid)
                holder.len.setText(panelArray[position].len)
                holder.qty.setText(panelArray[position].qty)
            }

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyAdapterPanel.ViewHolder {

            val v =LayoutInflater.from(parent.context).inflate(R.layout.item,parent,false)
            return ViewHolder(v)
        }

        override fun getItemCount(): Int {
            return panelArray.size
        }

        inner class ViewHolder(itemView:View):RecyclerView.ViewHolder(itemView) {
            val len =itemView.item_len as EditText
            val wid =itemView.item_wid as EditText
            val qty =itemView.item_qty as EditText
            val img =itemView.item_img as ImageView
            val content =itemView.item_content as LinearLayout
            val clear =itemView.item_clear as LinearLayout
        }

    }

    inner class MyAdapterStock : RecyclerView.Adapter<MyAdapterStock.ViewHolder>(),TextWatcher{

        override fun afterTextChanged(s: Editable?) {


        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {

            holder.wid.addTextChangedListener(object :TextWatcher{
                override fun afterTextChanged(s: Editable?) {

                }

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (s?.length!! >0){
                        holder.img.visibility=View.VISIBLE
                    }

                    if(holder.wid.text.isEmpty()&&holder.len.text.isEmpty()&&holder.qty.text.isEmpty()){
                        holder.img.visibility=View.GONE
                    }
                    if(stockArray.size-1==position){
                        if(stockArray.size<10) {
                            stockArray.add(dataStock("", "", ""))
                        }
                    }
                    stockArray[position].wid = holder.wid.text.toString()

//                    Toast.makeText(holder.len.context,"stock position : $position",Toast.LENGTH_LONG).show()
                }

            })

            holder.len.addTextChangedListener(object :TextWatcher{
                override fun afterTextChanged(s: Editable?) {

                }

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (s?.length!! >0){
                        holder.img.visibility=View.VISIBLE
                    }

                    if(holder.wid.text.isEmpty()&&holder.len.text.isEmpty()&&holder.qty.text.isEmpty()){
                        holder.img.visibility=View.GONE
                    }
                    if(stockArray.size-1==position){
                        if(stockArray.size<10) {
                            stockArray.add(dataStock("", "", ""))
                        }
                    }
                    stockArray[position].len = holder.len.text.toString()

//                    Toast.makeText(holder.len.context,"stock position : $position",Toast.LENGTH_LONG).show()
                }

            })

            holder.qty.addTextChangedListener(object :TextWatcher{
                override fun afterTextChanged(s: Editable?) {

                }

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (s?.length!! >0){
                        holder.img.visibility=View.VISIBLE
                    }

                    if(holder.wid.text.isEmpty()&&holder.len.text.isEmpty()&&holder.qty.text.isEmpty()){
                        holder.img.visibility=View.GONE
                    }
                    if(stockArray.size-1==position){
                        if(stockArray.size<10) {
                            stockArray.add(dataStock("", "", ""))
                        }
                    }
                    stockArray[position].qty = holder.qty.text.toString()

//                    Toast.makeText(holder.len.context,"stock position : $position",Toast.LENGTH_LONG).show()
                }


            })

            holder.clear.setOnClickListener {
                stockArray[position].wid = ""
                stockArray[position].len = ""
                stockArray[position].qty = ""
                holder.wid.text.clear()
                holder.len.text.clear()
                holder.qty.text.clear()
                holder.img.visibility=View.GONE
            }

            if (stockArray.size > 0){
                holder.wid.setText(stockArray[position].wid)
                holder.len.setText(stockArray[position].len)
                holder.qty.setText(stockArray[position].qty)
            }

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyAdapterStock.ViewHolder {

            val v =LayoutInflater.from(parent.context).inflate(R.layout.item,parent,false)
            return ViewHolder(v)
        }

        override fun getItemCount(): Int {
            return stockArray.size
        }

        inner class ViewHolder(itemView:View):RecyclerView.ViewHolder(itemView) {
            val len =itemView.item_len as EditText
            val wid =itemView.item_wid as EditText
            val qty =itemView.item_qty as EditText
            val img =itemView.item_img as ImageView
            val content =itemView.item_content as LinearLayout
            val clear =itemView.item_clear as LinearLayout
        }

    }

    inner class CustomAdapter(var list:ArrayList<Panel>): RecyclerView.Adapter<CustomAdapter.ViewHolder>(){
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.item_res,null)
            return ViewHolder(v)

        }

        override fun getItemCount(): Int {
            var count = 0
            for (i in list){
                if (i.check!!){
                    count += 1
                }
            }
            return count
        }


        @RequiresApi(Build.VERSION_CODES.KITKAT)
        override fun onBindViewHolder(holder:ViewHolder, position: Int) {
            val info = list[position]
            holder.img.setImageBitmap(showPanel(info))
        }


        @RequiresApi(Build.VERSION_CODES.KITKAT)
        private fun showPanel(p: Panel): Bitmap? {
            val widPanel = p.wid
            val highPanel = p.high
            val widScreen = applicationContext.resources.displayMetrics.widthPixels
            val padding = 50
            val rateDraw: Double
            val paint = Paint()
            val high:Int?
            val wid: Int?

            val bitmap = Bitmap.createBitmap(
                widScreen, // Width
                widScreen, // Height
                Bitmap.Config.ARGB_8888 // Config
            )

            // Initialize a new Canvas instance
            val canvas = Canvas(bitmap)
            canvas.drawColor(Color.LTGRAY)
            // ----------- assign to rate between high and width

            if (widPanel >= highPanel) {
                rateDraw = ((widScreen - 2 * padding).toDouble() / widPanel.toDouble())
                wid = (widScreen - 2 * padding)
                high = (rateDraw * highPanel).roundToInt()
                bitmap.height = high + padding * 2
            }else{
                high = (widScreen - 2 * padding)
                rateDraw = ((widScreen - 2 * padding) / highPanel.toDouble())
                wid = (rateDraw * widPanel).roundToInt()
                bitmap.height = high + padding * 2
            }

            // ---------------------- Draw Panel-------------

            paint.strokeWidth = 1f
            paint.color = Color.BLACK
            canvas.drawRect(
                padding.toFloat(),
                padding.toFloat(),
                (wid+padding).toFloat(),  // wid = Math.round(widPanel * rateDraw).toFloat()
                (high+padding).toFloat(),    //high = Math.round(highPanel * rateDraw).toFloat()
                paint
            )
            // ---------draw line about square ----------
            paint.color = Color.RED
            canvas.drawLine(
                padding.toFloat(),          // x
                (padding - 5).toFloat(),    // y
                (wid+padding).toFloat(),    // wid = Math.round(widPanel * rateDraw).toFloat()
                (padding - 5).toFloat(),    //high = Math.round(highPanel * rateDraw).toFloat()
                paint
            )
            var x_cent = padding.toFloat() + (wid+padding).toFloat()
            canvas.drawText(widPanel.toString(),x_cent/2,(padding - 7).toFloat(),paint)

            canvas.drawLine(
                (padding - 5).toFloat(),     // y
                padding.toFloat(),           // x ,
                (padding - 5).toFloat(),     // wid = Math.round(widPanel * rateDraw).toFloat()
                (high+padding).toFloat(),     //high = Math.round(highPanel * rateDraw).toFloat()
                paint
            )
            x_cent = padding.toFloat() + (high+padding).toFloat()
            canvas.drawText(highPanel.toString(),(padding - 30).toFloat(),x_cent/2,paint)

            //----------end draw line ----------------
            paint.strokeWidth = 0f
            paint.color = Color.WHITE
            canvas.drawRect(
                (padding + 1).toFloat(),
                (padding + 1).toFloat(),
                (wid - 1).toFloat()+padding,   // wid = Math.round(widPanel * rateDraw)
                (high - 1).toFloat()+padding,  // high = Math.round(highPanel * rateDraw)
                paint
            )

            //--------------------------- End Draw Panel ----------

            val len = p.item?.size
            for (i in 0 until len!!) {

                paint.strokeWidth = 1f
                paint.color = Color.BLACK
                paint.style = Paint.Style.STROKE
                val s = p.item!!.get(i)
                canvas.drawRect(
                    (padding + (s.point?.x!!.toFloat() * rateDraw).roundToInt()).toFloat(),
                    (padding + (s.point?.y!!.toFloat() * rateDraw).roundToInt()).toFloat(),
                    (padding.toLong() + (s.point?.x!!.toFloat() * rateDraw).roundToInt() + (s.wid * rateDraw).roundToInt()).toFloat()-1,
                    (padding.toLong() + (s.high * rateDraw).roundToInt() + (s.point?.y!!.toFloat() * rateDraw).roundToInt()).toFloat()-1,
                    paint
                )

                paint.strokeWidth = 0f
                paint.color = Color.CYAN
                paint.style = Paint.Style.FILL

                canvas.drawRect(
                    (padding + (s.point?.x!!.toFloat() * rateDraw).roundToInt()).toFloat()+1,
                    (padding + (s.point?.y!!.toFloat() * rateDraw).roundToInt()).toFloat()+1,
                    (padding.toLong() + (s.point?.x!!.toFloat() * rateDraw).roundToInt() + (s.wid * rateDraw).roundToInt()).toFloat()-1,
                    (padding.toLong() + (s.high * rateDraw).roundToInt() + (s.point?.y!!.toFloat() * rateDraw).roundToInt()).toFloat()-1,
                    paint
                )

                paint.strokeWidth = 1f
                paint.color = Color.BLACK
                paint.style = Paint.Style.STROKE

                val x_c = ((padding.toLong() + (s.point?.x!!.toFloat() * rateDraw).roundToInt() + (s.wid * rateDraw).roundToInt()).toFloat()-1) + (padding + (s.point?.x!!.toFloat() * rateDraw).roundToInt()).toFloat()
                val y_c = ((padding.toLong() + (s.high * rateDraw).roundToInt() + (s.point?.y!!.toFloat() * rateDraw).roundToInt()).toFloat()-1) + (padding + (s.point?.y!!.toFloat() * rateDraw).roundToInt()).toFloat()
                if(i<10) {
                    canvas.drawText(i.toString(),x_c/2,y_c/2,paint)
                }else{
                    canvas.drawText(i.toString(),x_c/2-2,y_c/2-2,paint)
                }

                // Display the newly created bitmap on app interface
            }

            return bitmap
        }

        inner class ViewHolder(itemView:View):RecyclerView.ViewHolder(itemView){
            val img = itemView.img_res as ImageView
        }

    }

    @SuppressLint("StaticFieldLeak")
    inner class MyTask: AsyncTask<Void, Void, Boolean>() {

        override fun doInBackground(vararg params: Void?): Boolean {

            val g = Gene()
            ch = g.fit(shapeData, panelData)
            if (ch == true){
                chromosome = g.getSolution()!!
            }
            return ch as Boolean
        }

        override fun onPreExecute() {
            super.onPreExecute()
            progressBar.visibility=View.VISIBLE
        }

        override fun onPostExecute(result: Boolean?) {
            super.onPostExecute(result)
            progressBar.visibility=View.GONE

        }


    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private fun showP(p: Panel): Bitmap {
        val widPanel = p.wid
        val highPanel = p.high
        val widScreen = applicationContext.resources.displayMetrics.widthPixels
        val padding = 50
        val rateDraw: Double
        val paint = Paint()
        val high:Int?
        val wid: Int?

        val bitmap = Bitmap.createBitmap(
            widScreen, // Width
            widScreen, // Height
            Bitmap.Config.ARGB_8888 // Config
        )

        // Initialize a new Canvas instance
        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.LTGRAY)
        // ----------- assign to rate between high and width

        if (widPanel >= highPanel) {
            rateDraw = ((widScreen - 2 * padding).toDouble() / widPanel.toDouble())
            wid = (widScreen - 2 * padding)
            high = (rateDraw * highPanel).roundToInt()
            bitmap.height = high + padding * 2
        }else{
            high = (widScreen - 2 * padding)
            rateDraw = ((widScreen - 2 * padding) / highPanel.toDouble())
            wid = (rateDraw * widPanel).roundToInt()
            bitmap.height = high + padding * 2
        }

        // ---------------------- Draw Panel-------------

        paint.strokeWidth = 1f
        paint.color = Color.BLACK
        canvas.drawRect(
            padding.toFloat(),
            padding.toFloat(),
            (wid+padding).toFloat(),  // wid = Math.round(widPanel * rateDraw).toFloat()
            (high+padding).toFloat(),    //high = Math.round(highPanel * rateDraw).toFloat()
            paint
        )
        // ---------draw line about square ----------
        paint.color = Color.RED
        canvas.drawLine(
            padding.toFloat(),          // x
            (padding - 5).toFloat(),    // y
            (wid+padding).toFloat(),    // wid = Math.round(widPanel * rateDraw).toFloat()
            (padding - 5).toFloat(),    //high = Math.round(highPanel * rateDraw).toFloat()
            paint
        )
        var x_cent = padding.toFloat() + (wid+padding).toFloat()
        canvas.drawText(widPanel.toString(),x_cent/2,(padding - 7).toFloat(),paint)

        canvas.drawLine(
            (padding - 5).toFloat(),     // y
            padding.toFloat(),           // x ,
            (padding - 5).toFloat(),     // wid = Math.round(widPanel * rateDraw).toFloat()
            (high+padding).toFloat(),     //high = Math.round(highPanel * rateDraw).toFloat()
            paint
        )
        x_cent = padding.toFloat() + (high+padding).toFloat()
        canvas.drawText(highPanel.toString(),(padding - 30).toFloat(),x_cent/2,paint)

        //----------end draw line ----------------
        paint.strokeWidth = 0f
        paint.color = Color.WHITE
        canvas.drawRect(
            (padding + 1).toFloat(),
            (padding + 1).toFloat(),
            (wid - 1).toFloat()+padding,   // wid = Math.round(widPanel * rateDraw)
            (high - 1).toFloat()+padding,  // high = Math.round(highPanel * rateDraw)
            paint
        )

        //--------------------------- End Draw Panel ----------

        val len = p.item?.size
        for (i in 0 until len!!) {

            paint.strokeWidth = 1f
            paint.color = Color.BLACK
            paint.style = Paint.Style.STROKE
            val s = p.item!!.get(i)
            canvas.drawRect(
                (padding + (s.point?.x!!.toFloat() * rateDraw).roundToInt()).toFloat(),
                (padding + (s.point?.y!!.toFloat() * rateDraw).roundToInt()).toFloat(),
                (padding.toLong() + (s.point?.x!!.toFloat() * rateDraw).roundToInt() + (s.wid * rateDraw).roundToInt()).toFloat()-1,
                (padding.toLong() + (s.high * rateDraw).roundToInt() + (s.point?.y!!.toFloat() * rateDraw).roundToInt()).toFloat()-1,
                paint
            )

            paint.strokeWidth = 0f
            paint.color = Color.CYAN
            paint.style = Paint.Style.FILL

            canvas.drawRect(
                (padding + (s.point?.x!!.toFloat() * rateDraw).roundToInt()).toFloat()+1,
                (padding + (s.point?.y!!.toFloat() * rateDraw).roundToInt()).toFloat()+1,
                (padding.toLong() + (s.point?.x!!.toFloat() * rateDraw).roundToInt() + (s.wid * rateDraw).roundToInt()).toFloat()-1,
                (padding.toLong() + (s.high * rateDraw).roundToInt() + (s.point?.y!!.toFloat() * rateDraw).roundToInt()).toFloat()-1,
                paint
            )

            paint.strokeWidth = 1f
            paint.color = Color.BLACK
            paint.style = Paint.Style.STROKE

            val x_c = ((padding.toLong() + (s.point?.x!!.toFloat() * rateDraw).roundToInt() + (s.wid * rateDraw).roundToInt()).toFloat()-1) + (padding + (s.point?.x!!.toFloat() * rateDraw).roundToInt()).toFloat()
            val y_c = ((padding.toLong() + (s.high * rateDraw).roundToInt() + (s.point?.y!!.toFloat() * rateDraw).roundToInt()).toFloat()-1) + (padding + (s.point?.y!!.toFloat() * rateDraw).roundToInt()).toFloat()
            if(i<10) {
                canvas.drawText(i.toString(),x_c/2,y_c/2,paint)
            }else{
                canvas.drawText(i.toString(),x_c/2-2,y_c/2-2,paint)
            }

            // Display the newly created bitmap on app interface
        }

        return bitmap
    }


    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private fun drawImage(){
        var count = 0
        for (i in img){
            saveTempBitmap(i,count)
            count += 1
        }
    }

    private fun saveTempBitmap(bitmap: Bitmap,num:Int) {
        if (isExternalStorageWritable()) {
            saveImage(bitmap,num)   // for image
//            savePDF(bitmap)   // for pdf
            Toast.makeText(this,"Done",Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(this,"Error",Toast.LENGTH_LONG).show()
        }
    }

    private fun saveTempPdf() {
        if (isExternalStorageWritable()) {
            savePDF()   // for pdf
            Toast.makeText(this,"Done",Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(this,"Error",Toast.LENGTH_LONG).show()
        }
    }

    private fun saveImage(finalBitmap: Bitmap,num:Int) {

        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val fname = "Cutting_"+System.currentTimeMillis()+"$num.jpg"  // $timeStamp

//        val file = File(applicationContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES), fname) // myDir
        val file = File("/storage/emulated/0/Cutting/Images",fname)
        if (file.exists()) file.delete()
        try {
            file.parentFile.mkdirs()   // for create own folder in folder
            val out = FileOutputStream(file)
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
            out.flush()
            out.close()

        } catch (e: Exception) {
            Toast.makeText(this,"Error : in Saved - "+e.message.toString(),Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }

    }

    private fun isExternalStorageWritable(): Boolean {
        val state = Environment.getExternalStorageState()
        return if (Environment.MEDIA_MOUNTED.equals(state)) {
            true
        } else false
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun checkPermission(permission:String, requestCode:Int){
        if(requestCode == imgCode){
            if (checkSelfPermission(permission) == PackageManager.PERMISSION_DENIED) {
                requestPermissions(arrayOf(permission), requestCode)
            } else {
                drawImage()
            }
        }
        if(requestCode == pdfCode){
            if (checkSelfPermission(permission) == PackageManager.PERMISSION_DENIED) {
                requestPermissions(arrayOf(permission), requestCode)
            } else {
                saveTempPdf()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == imgCode){
            if (grantResults.isNotEmpty() && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                drawImage()
            }else{
                Toast.makeText(this,"Deny",Toast.LENGTH_LONG).show()
            }
        }

        if (requestCode == pdfCode){
            if (grantResults.isNotEmpty() && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                saveTempPdf()
            }else{
                Toast.makeText(this,"Deny",Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun savePDF(){
        try
        {
            val filename = "/storage/emulated/0/Cutting/Cutting_"+System.currentTimeMillis()+".pdf"
            val document = com.itextpdf.text.Document()

            PdfWriter.getInstance(document,  FileOutputStream(filename));
            document.open();
            for (i in img)
                addImage(document,i);
            document.close();
        } catch ( e:Exception){
            e.printStackTrace();
        }
    }

    private fun addImage(document:com.itextpdf.text.Document,img:Bitmap){

        try
        {
            val bytes = ByteArrayOutputStream()
            val pmb = img
            pmb.compress(Bitmap.CompressFormat.JPEG,100,bytes)
            val bitmap = bytes.toByteArray()
            val image = com.itextpdf.text.Image.getInstance(bitmap)
            val scaler = ((document.pageSize.width - document.leftMargin() - document.rightMargin() - 0 )/image.width)*100
            image.scalePercent(scaler)
            image.alignment = Image.ALIGN_CENTER
            document.add(image)

        } catch (e:Exception) {
            Toast.makeText(this, e.message.toString(), Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }

    }

}
