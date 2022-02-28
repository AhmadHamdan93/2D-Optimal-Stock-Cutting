package com.example.cuttingsolver.cuttingAlgorithm

import kotlin.random.Random

data class Point(var x:Int?=0, var y:Int?=0, var dir:String?=null)

data class Shape(var wid:Int, var high:Int, var point: Point?=null)

data class Panel(var wid:Int, var high:Int, var item:ArrayList<Shape>? = ArrayList(), var point:ArrayList<Point>? = ArrayList(),
                 var check:Boolean?=false, var score:Int?=0, var max_wid:Int?=0, var max_high:Int?=0){
    init {
        point?.add(Point(0,0,"root"))
    }
}

data class Chromosome(private var shape:ArrayList<Shape>? = ArrayList(),
                      var pnl:ArrayList<Panel>? = ArrayList(), var score:Int? = -1) {

    fun putPanel(panel: ArrayList<Panel>){
        for (i in panel){
            this.pnl?.add(i)
        }
    }

    fun putShape(rec:ArrayList<Shape>){
        shape?.clear()
        for (i in rec){
            this.shape?.add(i)
        }
    }

    fun getShape(): ArrayList<Shape> {
        this.shape?.clear()
        for (p in this.pnl!!){
            for (rec in p.item!!){
                val s = Shape(rec.wid,rec.high)
                s.point = rec.point
                this.shape?.add(s)
            }
        }
        return this.shape!!
    }

}

data class Checked(var check: Boolean = false , var point: Point = Point())

data class Cutting(var palette:ArrayList<Panel>?=ArrayList(), var data:ArrayList<Shape>?=ArrayList(), var linear:String?=""){
    private fun compare(sample: Shape, arrayRec:ArrayList<Shape>):Boolean{
        for (item in arrayRec){
            if ((item.wid == sample.wid) && (item.high == sample.high))
                return false
            if ((sample.wid == item.high)&&(sample.high == item.wid))
                return false
        }
        return true
    }
    private fun getIndex(sample: Shape, arrayRec: ArrayList<Shape>):Int{
        var count = 0
        for (item in arrayRec){
            if ((item.wid == sample.wid) && (item.high == sample.high))
                return count
            if ((sample.wid == item.high)&&(sample.high == item.wid))
                return count
            count += 1
        }
        return 0
    }
    private fun sortDataFrq(){
        val tempData = ArrayList<Shape>()
        val tempIndex = ArrayList<Int>()
        val sortData = ArrayList<Shape>()
        for (item in this.data!!){
            if (compare(item , tempData)){
                tempData.add(item)
                tempIndex.add(1)
            }else{
                val idx = getIndex(item,tempData)
                tempIndex[idx] += 1
            }
        }

        for (i in 0 until tempIndex.size-1){
            for (j in i until tempIndex.size) {
                if (tempIndex[i] < tempIndex[j]) {
                    val temp = tempIndex[i]
                    tempIndex[i] = tempIndex[j]
                    tempIndex[j] = temp
                    val t = tempData[i]
                    tempData[i] = tempData[j]
                    tempData[j] = t
                }
            }
        }

        for (i in 0 until tempData.size){
            for (j in 0 until tempIndex[i]){
                sortData.add(Shape(tempData[i].wid,tempData[i].high,tempData[i].point))
            }
        }
        data!!.clear()
        for (i in sortData){
            data!!.add(i)
        }
    }
    private fun sortDataLTS(){
        for (idx in 0 until data!!.size-1){
            for (j in idx until data!!.size){
                if (data!![idx].high < data!![j].high){
                    val t = data!![idx]
                    data!![idx] = data!![j]
                    data!![j] = t
                }
            }
        }

        for (idx in 0 until data!!.size-1){
            for (j in idx until data!!.size){
                if (data!![idx].wid < data!![j].wid){
                    val t = data!![idx]
                    data!![idx] = data!![j]
                    data!![j] = t
                }
            }
        }
    }
    private fun sortDataSTL(){
        for (idx in 0 until data!!.size-1){
            for (j in idx until data!!.size){
                if (data!![idx].wid > data!![j].wid){
                    val t = data!![idx]
                    data!![idx] = data!![j]
                    data!![j] = t
                }
            }
        }
        for (idx in 0 until data!!.size-1){
            for (j in idx until data!!.size){
                if (data!![idx].high > data!![j].high){
                    val t = data!![idx]
                    data!![idx] = data!![j]
                    data!![j] = t
                }
            }
        }

    }
    private fun sortPoint(point: ArrayList<Point>){
        for (idx in 0 until point.size-1){
            for (j in idx until point.size){
                if (point[idx].x!! > point[j].x!!){
                    val temp = point[idx]
                    point[idx] = point[j]
                    point[j] = temp
                }
                if (point[idx].x!! == point[j].x!!){
                    if (point[idx].y!! > point[j].y!!){
                        val t = point[idx]
                        point[idx] = point[j]
                        point[j] = t
                    }
                }
            }
        }
    }
    private fun sortPoint1(point: ArrayList<Point>){
        for (idx in 0 until point.size-1){
            for (j in idx until point.size){
                if (point[idx].y!! > point[j].y!!){
                    val temp = point[idx]
                    point[idx] = point[j]
                    point[j] = temp
                }
                if (point[idx].y!! == point[j].y!!){
                    if (point[idx].x!! > point[j].x!!){
                        val t = point[idx]
                        point[idx] = point[j]
                        point[j] = t
                    }
                }
            }
        }
    }
    private fun checkRootRec(mainPoint: Point, testPoint: Point, shape:ArrayList<Shape>):Boolean{
        for (r in shape){
            if ((testPoint.dir == "hor")&&(testPoint.y == r.point?.y)){
                if (r.wid+ r.point?.x!! == testPoint.x){
                    if ((mainPoint.x!! < r.point!!.x!!)&&(mainPoint.y!! < r.point!!.y!!)){
                        if (r.point!!.dir == "ver"){
                            return false
                        }else{
                            return true
                        }
                    }
                }
            }

            if ((testPoint.dir == "ver")&&(testPoint.x == r.point?.x)){
                if (r.high+ r.point?.y!! == testPoint.y){
                    if ((mainPoint.x!! < r.point!!.x!!)&&(mainPoint.y!! < r.point!!.y!!)){
                        if (r.point!!.dir == "hor"){
                            return false
                        }else{
                            return true
                        }
                    }
                }
            }
        }
        return true
    }
    private fun findNearPoint(pnt: Point, points:ArrayList<Point>, rect:ArrayList<Shape>):ArrayList<Point> {
        val nearPoint = ArrayList<Point>()
        var wMin = 0
        var hMin= 0
        for (pt in points) {
            if (pt.dir == "hor") {
                if ((pt.x!! > pnt.x!!) && (pt.y!! > pnt.y!!)) {
                    if (checkRootRec(pnt, pt, rect)) {
                        if ((wMin == 0) && (hMin == 0)) {
                            nearPoint.add(pt)
                            wMin = pt.x!!
                            hMin = pt.y!!
                        } else {
                            if (wMin > pt.x!!) {
                                wMin = pt.x!!
//                                nearPoint[0].x = wMin
                                nearPoint[0].x = wMin
                                nearPoint[0].y = hMin
                                nearPoint[0].dir = "hor"
//                                nearPoint[0] = com.example.cuttingsolver.cuttingAlgorithm.Point(wMin, hMin, "hor")
                            }
                            if (hMin > pt.y!!) {
                                hMin = pt.y!!
                                nearPoint[0].x = wMin
                                nearPoint[0].y = hMin
                                nearPoint[0].dir = "hor"
//                                nearPoint[0].y = hMin //= com.example.cuttingsolver.cuttingAlgorithm.Point(wMin, hMin, "hor")
                            }
                        }
                    }
                }
            }
        }


        var wMin1 = 0
        var hMin1 = 0
        for (pt in points) {
            if (pt.dir == "ver") {
                if ((pt.x!! > pnt.x!!) && (pt.y!! > pnt.y!!)) {
                    if (checkRootRec(pnt, pt, rect)) {
                        if ((wMin1 == 0) && (hMin1 == 0)) {
                            nearPoint.add(pt)
                            wMin1 = pt.x!!
                            hMin1 = pt.y!!
                        } else {
                            if (nearPoint.size == 2) {
                                if (wMin1 > pt.x!!) {
                                    wMin1 = pt.x!!
                                    nearPoint[1].x = wMin1
                                    nearPoint[1].y = hMin1
                                    nearPoint[1].dir = "ver"
                                    //com.example.cuttingsolver.cuttingAlgorithm.Point(wMin1, hMin1, "ver")
                                }
                                if (hMin1 > pt.y!!) {
                                    hMin1 = pt.y!!
//                                    nearPoint[1] = com.example.cuttingsolver.cuttingAlgorithm.Point(wMin1, hMin1, "ver")
                                    nearPoint[1].x = wMin1
                                    nearPoint[1].y = hMin1
                                    nearPoint[1].dir = "ver"
                                }
                            } else {
                                if (wMin1 > pt.x!!) {
                                    wMin1 = pt.x!!
                                    nearPoint[0].x = wMin1
                                    nearPoint[0].y = hMin1
                                    nearPoint[0].dir = "ver"
//                                    nearPoint[0] = com.example.cuttingsolver.cuttingAlgorithm.Point(wMin1, hMin1, "ver")
                                }
                                if (hMin1 > pt.y!!) {
                                    hMin1 = pt.y!!
                                    nearPoint[0].x = wMin1
                                    nearPoint[0].y = hMin1
                                    nearPoint[0].dir = "ver"
//                                    nearPoint[0] = com.example.cuttingsolver.cuttingAlgorithm.Point(wMin1, hMin1, "ver")
                                }

                            }
                        }
                    }
                }
            }
        }
        return nearPoint
    }
    private fun checkPoint(point: Point, rect: ArrayList<Shape>):Boolean{
        for (item in rect){
            if ((item.point?.x == point.x)&&(item.point?.y == point.y)){
                return false
            }
            if ((item.point?.x!! < point.x!!)||(item.point?.y!! < point.y!!)){
                if (item.point?.x == point.x) {
                    if (item.point?.y!! + item.high > point.y!!) {
                        return false
                    }
                }
                if (item.point!!.y == point.y){
                    if (item.point?.x!! + item.wid > point.x!!){
                        return false
                    }
                }

            }
        }
        return true

    }
    private fun checkInsert(rectangle: Shape, panel: Panel): Checked {
        for (p in panel.point!!){
            var w = panel.wid
            var h = panel.high

            if((rectangle.wid + p.x!! <= w)&&(rectangle.high + p.y!! <= h)){
                if (checkPoint(p,panel.item!!)){
                    val near:ArrayList<Point> = findNearPoint(p,panel.point!!,panel.item!!)
                    if (near.size == 2){
                        val hor = near[0]
                        if (hor.dir == "hor"){
                            h = hor.y!!
                        }
                        val ver = near[1]
                        if (ver.dir == "ver"){
                            w = ver.x!!
                        }
                    }else{
                        if (near.size == 1){
                            val hor = near[0]
                            if (hor.dir == "hor"){
                                h = hor.y!!
                            }else{
                                w = hor.x!!
                            }
                        }
                    }
                    if (((w - rectangle.wid -p.x!!) >= 0) && ((h - rectangle.high -p.y!!) >= 0)){
                        return Checked(true,p)
                    }
                }
            }

            else{
                val t = rectangle.high
                rectangle.high = rectangle.wid
                rectangle.wid = t
                if((rectangle.wid + p.x!! <= w)&&(rectangle.high + p.y!! <= h)){
                    if (checkPoint(p,panel.item!!)){
                        val near:ArrayList<Point> = findNearPoint(p,panel.point!!,panel.item!!)
                        if (near.size == 2){
                            val hor = near[0]
                            if (hor.dir == "hor"){
                                h = hor.y!!
                            }
                            val ver = near[1]
                            if (ver.dir == "ver"){
                                w = ver.x!!
                            }
                        }else{
                            if (near.size == 1){
                                val hor = near[0]
                                if (hor.dir == "hor"){
                                    h = hor.y!!
                                }else{
                                    w = hor.x!!
                                }
                            }
                        }
                        if (((w - rectangle.wid -p.x!!) >= 0) && ((h - rectangle.high -p.y!!) >= 0)){
                            return Checked(true,p)
                        }
                    }
                    else{
                        val temp = rectangle.high
                        rectangle.high = rectangle.wid
                        rectangle.wid = temp
                    }
                }
                else{
                    val temp = rectangle.high
                    rectangle.high = rectangle.wid
                    rectangle.wid = temp
                }
            }
        }
        return Checked(false, Point())
    }
    private fun cut():Boolean{
        for (rect in this.data!!){
            var c = true
            for (pnl in this.palette!!){

                if (pnl.wid >= pnl.high){
                    if (rect.wid > rect.high){
                        val t = rect.wid
                        rect.wid = rect.high
                        rect.high = t
                    }
                }else{
                    if (rect.wid < rect.high){
                        val t = rect.wid
                        rect.wid = rect.high
                        rect.high = t
                    }
                }

                val checked = checkInsert(rect,pnl)
                if (checked.check){
                    pnl.check = true
                    pnl.point?.add(Point(checked.point.x!! + rect.wid,checked.point.y!!,"hor"))
                    pnl.point?.add(Point(checked.point.x!!,checked.point.y!! + rect.high,"ver"))
                    rect.point =checked.point
                    pnl.item?.add(rect)
                    pnl.point?.remove(checked.point)
                    if (pnl.wid >= pnl.high){       // in code python the operator is <=
                        sortPoint(pnl.point!!)
                    }else{
                        sortPoint1(pnl.point!!)
                    }
                    c = false
                    break
                }
            }
            if (c){
                return false  // in python exist after for () else if not working code here is problem
            }

        }
        return true
    }
    fun fit(panel: ArrayList<Panel>, stock: ArrayList<Shape>, linSTG:String):Boolean{
        this.linear = linSTG
        this.palette?.clear()
        this.data?.clear()
        for (item in panel){
            this.palette?.add(item)
        }
        for (i in stock){
            this.data?.add(i)
        }

        if (linear == "stl"){
            sortDataSTL()
        }else{
            if (linear == "lts"){
                sortDataLTS()
            }else{
                if (linear == "frq"){
                    sortDataFrq()
                }
            }
        }

        return cut()

    }
    fun getSolution():ArrayList<Panel>{
        val temp = ArrayList<Panel>()
        for (item in this.palette!!){
            temp.add(item)
        }
        return temp
    }
}

data class Gene(
    private var chromosome:ArrayList<Chromosome>?=ArrayList(), var data: ArrayList<Shape>?=ArrayList(), var panel: ArrayList<Panel>?=ArrayList(),
    var dataCopy:ArrayList<Shape>?=ArrayList(), var panelCopy:ArrayList<Panel>?=ArrayList(), var rateMutation: Double = 0.9){
    private fun primeGene():ArrayList<Chromosome>{
        val fourChromosome = ArrayList<Chromosome>()
        val cuttingType = arrayOf("","lts","stl","frq")
        val cutting = Cutting()
        for (i in 0 until 4){
            this.panel?.clear()
            this.data?.clear()
            readData()
            readPanel()
            if (cutting.fit(this.panel!!,this.data!!,cuttingType[i])){
                val p = cutting.getSolution()
                val chromosome = Chromosome()
                chromosome.putPanel(p)
                chromosome.score = evaluation(p)
                fourChromosome.add(chromosome)
            }
        }
        return fourChromosome
    }
    private fun readData(){
        this.data?.clear()
        for (item in this.dataCopy!!){
            this.data?.add(Shape(item.wid,item.high))
        }
    }
    private fun readPanel(){
        this.panel?.clear()
        for (i in this.panelCopy!!){
            this.panel?.add(Panel(i.wid,i.high))
        }
    }
    private fun readPanel1(){
        this.panel?.clear()
        for (i in this.panelCopy!!){
            this.panel?.add(Panel(i.high,i.wid))
        }
    }
    private fun findMaxWH(points: ArrayList<Point>):ArrayList<Int>{
        var mWid = 0
        var mHigh = 0
        for (pt in points){
            if (pt.x!! > mWid){
                mWid = pt.x!!
            }
            if (pt.y!! > mHigh){
                mHigh = pt.y!!
            }
        }
        val a = ArrayList<Int>()
        a.add(mWid)
        a.add(mHigh)
        return a
    }
    private fun sumSQR(item: ArrayList<Shape>):Int{
        var res = 0
        for (rec in item){
            res += (rec.wid*rec.high)
        }
        return res
    }
    private fun evaluation(palette: ArrayList<Panel>):Int{
        var pnlScore = 0
        for (pnl in palette){
            if (pnl.check!!){
                val a = findMaxWH(pnl.point!!)
                val square = sumSQR(pnl.item!!)
                pnl.max_wid = a[0]
                pnl.max_high = a[1]
                pnl.score = a[0] * a[1] - square
                pnlScore += pnl.score!!
            }
        }
        return pnlScore
    }
    private fun checkRec(rec1: Shape, rec2: Shape):Boolean{
        if ((rec1.wid == rec2.wid)&&(rec1.high == rec2.high)){
            return true
        }
        if ((rec1.wid == rec2.high)&&(rec1.wid == rec2.high)){
            return true
        }
        return false
    }
    private fun cross(rand:Int, data1: ArrayList<Shape>, data2: ArrayList<Shape>):ArrayList<Shape>{
        val tempData = ArrayList<Shape>()
        val d2 = ArrayList<Shape>()
        for (item in data2){
            d2.add(Shape(item.wid,item.high))
        }
        for (i in 0 until rand){
            if (rand < data1.size-1) {
                tempData.add(Shape(data1[i].wid, data1[i].high))
            }
        }
        for (i in 0 until rand){
            for (j in d2){
                if (checkRec(data1[i],j)){
                    d2.remove(j)
                    break
                }
            }
        }
        for (item in d2){
            tempData.add(Shape(item.wid,item.high))
        }
        return tempData
    }
    private fun crossover(chromosome: ArrayList<Chromosome>):ArrayList<Chromosome>{
        val ch4 = ArrayList<Chromosome>()
        val fourChromosome = ArrayList<Chromosome>()

        for (item in chromosome){
            ch4.add(item)
        }
        // ----------------  copy rectangle -------------------
        val shapeData1 = ch4[0].getShape()
        val shapeData2 = ch4[1].getShape()
        val shapeData3 = ch4[2].getShape()
        val shapeData4 = ch4[3].getShape()
        // ----------------  solution cross ------------------
        val rand = Random.nextInt(shapeData1.size-1)
        val shapeData11 = cross(rand,shapeData1,shapeData2)
        val shapeData22 = cross(rand,shapeData2,shapeData1)

        val rand2 = Random.nextInt(shapeData3.size-1)
        val shapeData33 = cross(rand2,shapeData3,shapeData4)
        val shapeData44 = cross(rand2,shapeData4,shapeData3)
        // ------------------ cutting for first data ---------
        val cutting= Cutting()
        panel?.clear()
        readPanel()
        if (cutting.fit(panel!!,shapeData11,"")){
            val p1 = cutting.getSolution()
            val chromosome1 = Chromosome()
            chromosome1.putPanel(p1)
            chromosome1.score = evaluation(p1)
            fourChromosome.add(chromosome1)
        }
        // ------------------ cutting for second data --------
        val cutting1= Cutting()
        panel?.clear()
        readPanel()
        if (cutting1.fit(panel!!,shapeData22,"")){
            val p2 = cutting1.getSolution()
            val chromosome2 = Chromosome()
            chromosome2.putPanel(p2)
            chromosome2.score = evaluation(p2)
            fourChromosome.add(chromosome2)
        }
        // --------------- cutting for third data---------
        val cutting2= Cutting()
        panel?.clear()
        readPanel()
        if (cutting2.fit(panel!!,shapeData33,"")){
            val p3 = cutting2.getSolution()
            val chromosome3 = Chromosome()
            chromosome3.putPanel(p3)
            chromosome3.score = evaluation(p3)
            fourChromosome.add(chromosome3)
        }
        // ------------- cutting for four data ---------
        val cutting3= Cutting()
        panel?.clear()
        readPanel()
        if (cutting3.fit(panel!!,shapeData44,"")){
            val p4 = cutting3.getSolution()
            val chromosome4 = Chromosome()
            chromosome4.putPanel(p4)
            chromosome4.score = evaluation(p4)
            fourChromosome.add(chromosome4)
        }
        shapeData1.clear()
        shapeData2.clear()
        shapeData3.clear()
        shapeData4.clear()
        shapeData11.clear()
        shapeData22.clear()
        shapeData33.clear()
        shapeData44.clear()

        return fourChromosome
    }
    private fun mutate(rectangle:ArrayList<Shape>):ArrayList<Shape>{
        val tempData = ArrayList<Shape>()
        for (item in rectangle){
            val a = Random.nextDouble()
            if (a < rateMutation){
                tempData.add(Shape(item.high,item.wid))
            }else{
                tempData.add(Shape(item.wid,item.high))
            }
        }
        return tempData
    }
    private fun mut(rectangle:ArrayList<Shape>):ArrayList<Shape>{
        val tempData = ArrayList<Shape>()
        val a = Random.nextDouble()
        if (a < rateMutation){
            val i = rectangle.size - 1
            for (idx in 0 until rectangle.size){
                tempData.add(Shape(rectangle[i - idx].wid,rectangle[i - idx].high))
            }
        }else{
            for (item in rectangle){
                tempData.add(Shape(item.wid,item.high))
            }
        }
        return rectangle
    }
    private fun mutation(chromosome: ArrayList<Chromosome>):ArrayList<Chromosome>{
        val ch4 = ArrayList<Chromosome>()
        val fourChromosome = ArrayList<Chromosome>()

        for (item in chromosome){
            ch4.add(item)
        }
        // ----------------  copy rectangle -------------------
        val shapeData1 = ch4[0].getShape()
        val shapeData2 = ch4[1].getShape()
        val shapeData3 = ch4[2].getShape()
        val shapeData4 = ch4[3].getShape()
        // ----------------  solution cross ------------------

        val shapeData11 = mut(shapeData1)   // mutate
        val shapeData22 = mut(shapeData2)

        val shapeData33 = mut(shapeData3)
        val shapeData44 = mut(shapeData4)
        // ------------------ cutting for first data ---------
        val cutting= Cutting()
        panel?.clear()
        val randNum1 = Random.nextDouble()
        if (randNum1 < rateMutation) {
            readPanel()
        }else{
            readPanel1()
        }
        if (cutting.fit(panel!!,shapeData11,"")){
            val p1 = cutting.getSolution()
            val chromosome1 = Chromosome()
            chromosome1.putPanel(p1)
            chromosome1.score = evaluation(p1)
            fourChromosome.add(chromosome1)
        }
        // ------------------ cutting for second data --------
        val cutting1= Cutting()
        panel?.clear()
        val randNum2 = Random.nextDouble()
        if (randNum2 < rateMutation) {
            readPanel()
        }else{
            readPanel1()
        }
        if (cutting1.fit(panel!!,shapeData22,"")){
            val p2 = cutting1.getSolution()
            val chromosome2 = Chromosome()
            chromosome2.putPanel(p2)
            chromosome2.score = evaluation(p2)
            fourChromosome.add(chromosome2)
        }
        // --------------- cutting for third data---------
        val cutting2= Cutting()
        panel?.clear()
        val randNum3 = Random.nextDouble()
        if (randNum3 < rateMutation) {
            readPanel()
        }else{
            readPanel1()
        }
        if (cutting2.fit(panel!!,shapeData33,"")){
            val p3 = cutting2.getSolution()
            val chromosome3 = Chromosome()
            chromosome3.putPanel(p3)
            chromosome3.score = evaluation(p3)
            fourChromosome.add(chromosome3)
        }
        // ------------- cutting for four data ---------
        val cutting3= Cutting()
        panel?.clear()
        val randNum4 = Random.nextDouble()
        if (randNum4 < rateMutation) {
            readPanel()
        }else{
            readPanel1()
        }
        if (cutting3.fit(panel!!,shapeData44,"")){
            val p4 = cutting3.getSolution()
            val chromosome4 = Chromosome()
            chromosome4.putPanel(p4)
            chromosome4.score = evaluation(p4)
            fourChromosome.add(chromosome4)
        }
        shapeData1.clear()
        shapeData2.clear()
        shapeData3.clear()
        shapeData4.clear()
        shapeData11.clear()
        shapeData22.clear()
        shapeData33.clear()
        shapeData44.clear()

        return fourChromosome
    }
    private fun selection(chromosome: ArrayList<Chromosome>, chromosomeCross: ArrayList<Chromosome>):ArrayList<Chromosome>{
        val resultChromosome = ArrayList<Chromosome>()
        val allChromosome = ArrayList<Chromosome>()
        for (item in chromosomeCross){
            if (item.score!! >= 0){
                val subSol = Chromosome()
                subSol.score = item.score
                subSol.putPanel(item.pnl!!)
                subSol.putShape(item.getShape())
                allChromosome.add(subSol)
            }
        }
        chromosomeCross.clear()
        for (item in chromosome){
            if (item.score!! >= 0){
                val subSol = Chromosome()
                subSol.score = item.score
                subSol.putPanel(item.pnl!!)
                subSol.putShape(item.getShape())
                allChromosome.add(subSol)
            }
        }
        chromosome.clear()
        for (i in 0 until allChromosome.size - 1){
            for (j in i until allChromosome.size){
                if (allChromosome[i].score!! > allChromosome[j].score!!){
                    val temp = allChromosome[i]
                    allChromosome[i] = allChromosome[j]
                    allChromosome[j] = temp
                }
            }
        }

        for (count in 0..3){
            val subSol = Chromosome()
            subSol.score = allChromosome[count].score
            subSol.putPanel(allChromosome[count].pnl!!)
            subSol.putShape(allChromosome[count].getShape())
            resultChromosome.add(subSol)
        }
        return resultChromosome
    }
    private fun mainGA():Boolean{
        var chromosomeA = primeGene()
        if (chromosomeA.size < 4){
            return false
        }
        var count = 0
        var bestScore = chromosomeA[0].score
        while ( (count < 300) && (bestScore!! > 100)){   //(count < 300) &&
            val chromosomeCross = crossover(chromosomeA)
            if (chromosomeCross.size < 4){
                return false
            }
            chromosomeA = selection(chromosomeA,chromosomeCross)
            val chromosomeMut = mutation(chromosomeA)   // val chromosomeMut
            if (chromosomeMut.size < 4){    // chromosomeMut
                return false
            }
            chromosomeA = selection(chromosomeA,chromosomeMut)
            count += 1
            bestScore = chromosomeA[0].score
            println("$count , "+chromosomeA[0].score.toString()+" , "+chromosomeA[1].score.toString()+" , "+chromosomeA[2].score.toString()+" , "+chromosomeA[3].score.toString())

        }
        this.chromosome = chromosomeA
        return true
    }
    fun fit(data_rec: ArrayList<Shape>, panel_rec: ArrayList<Panel>):Boolean{
        for (item in data_rec){
            data!!.add(Shape(item.wid,item.high))
            dataCopy!!.add(Shape(item.wid,item.high))
        }
        for (item in panel_rec){
            panel!!.add(Panel(item.wid,item.high))
            panelCopy!!.add(Panel(item.wid,item.high))
        }
        return mainGA()
    }
    fun getSolution(): ArrayList<Chromosome>? {
        return this.chromosome
    }
}