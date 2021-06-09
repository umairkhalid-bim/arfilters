package com.tv.Filters

import android.content.Context
import android.util.Log
import com.google.ar.core.AugmentedFace
import com.google.ar.sceneform.FrameTime
import com.google.ar.sceneform.math.Vector3
var printLog = false

class CustomFaceNode(augmentedFace: AugmentedFace?,
                 val context: Context,
                     val onMouth : (open : Boolean) -> Unit
): AugmentedFaceNode(augmentedFace) {
//
//    private var eyeNodeLeft: Node? = null
//    private var eyeNodeRight: Node? = null
//    private var mustacheNode: Node? = null
//    private var upperLip: Node? = null
//    private var downLip: Node? = null

    private var isMouthOpen = false


    companion object {
        enum class FaceRegion {
            LEFT_EYE,
            RIGHT_EYE,
            MUSTACHE,
            UP_LIP,
            DOWN_LIP,
            RIGHT_MOUTH,
        }
    }

    override fun onActivate() {
        super.onActivate()
//        eyeNodeLeft = Node()
//        eyeNodeLeft?.setParent(this)
//
//        eyeNodeRight = Node()
//        eyeNodeRight?.setParent(this)
//
//        mustacheNode = Node()
//        mustacheNode?.setParent(this)
//
//        upperLip = Node()
//        upperLip?.setParent(this)
//
//        downLip = Node()
//        downLip?.setParent(this)
//
//        ViewRenderable.builder()
//            .setView(context, R.layout.element_layout)
//            .build()
//            .thenAccept { uiRenderable: ViewRenderable ->
//                uiRenderable.isShadowCaster = false
//                uiRenderable.isShadowReceiver = false
//                eyeNodeLeft?.renderable = uiRenderable
//                eyeNodeRight?.renderable = uiRenderable
//            }
//            .exceptionally { throwable: Throwable? ->
//                throw AssertionError(
//                    "Could not create ui element",
//                    throwable
//                )
//            }
//
//        ViewRenderable.builder()
//            .setView(context, R.layout.element_layout)
//            .build()
//            .thenAccept { uiRenderable: ViewRenderable ->
//                uiRenderable.isShadowCaster = false
//                uiRenderable.isShadowReceiver = false
//                mustacheNode?.renderable = uiRenderable
//                uiRenderable.view.findViewById<ImageView>(R.id.element_image).setImageResource(R.drawable.mustache)
//            }
//            .exceptionally { throwable: Throwable? ->
//                throw AssertionError(
//                    "Could not create ui element",
//                    throwable
//                )
//            }
    }

    private fun getRegionPose(region: FaceRegion) : Vector3? {
        val buffer = augmentedFace?.meshVertices
        if (buffer != null) {
            return when (region) {
                FaceRegion.LEFT_EYE ->
                    Vector3(buffer.get(374 * 3),buffer.get(374 * 3 + 1),  buffer.get(374 * 3 + 2))
                FaceRegion.RIGHT_EYE ->
                    Vector3(buffer.get(145 * 3),buffer.get(145 * 3 + 1),  buffer.get(145 * 3 + 2))
                FaceRegion.MUSTACHE ->
                    Vector3(buffer.get(11 * 3),
                        buffer.get(11 * 3 + 1),
                        buffer.get(11 * 3 + 2))
                FaceRegion.UP_LIP ->
                    Vector3(buffer.get(13 * 3),
                        buffer.get(13 * 3 + 1),
                        buffer.get(13 * 3 + 2))
                FaceRegion.DOWN_LIP ->
                    Vector3(buffer.get(14 * 3),
                        buffer.get(14 * 3 + 1),
                        buffer.get(14 * 3 + 2))
                FaceRegion.RIGHT_MOUTH ->
                    Vector3(buffer.get(409 * 3),
                        buffer.get(409 * 3 + 1),
                        buffer.get(409 * 3 + 2))
            }
        }
        return null
    }

    override fun onUpdate(frameTime: FrameTime?) {
        super.onUpdate(frameTime)

        augmentedFace?.let {face ->


            getRegionPose(FaceRegion.UP_LIP)?.let { top ->


                val down = getRegionPose(FaceRegion.DOWN_LIP)
                val mouthOpen = (Vector3.angleBetweenVectors(down , top) > 15)

                if (isMouthOpen == mouthOpen)
                    return
                onMouth(mouthOpen)
                isMouthOpen = mouthOpen

//                val rightMouth = getRegionPose(FaceRegion.RIGHT_MOUTH)

                if (printLog) {


                    //Toast.makeText(context, (Vector3.angleBetweenVectors(down , top) > 15).toString(), Toast.LENGTH_SHORT).show()
                    Log.e("LIP_AXIS" , "Mouth Open->" + (Vector3.angleBetweenVectors(down , top) > 15).toString())

                    down?.let {
                        val x = (top.x - it.x)
                        val y = (top.y - it.y)
                        val z = (top.z - it.z)
                        //Log.e("LIP_AXIS" , "X->" + (c > 0).toString())
//                        Log.e("LIP_AXIS" , "X->" + c.toString())
//                        Log.e("LIP_AXIS" , "Y->" + y.toString())
//                        Log.e("LIP_AXIS" , "Y->" + (y > 0).toString())
//                        Log.e("LIP_AXIS" , "Z->" + z.toString())




//                        if ((top.x - it.x) > 1){
//                            Log.e("LIP_AXIS" , "FACE open")
//                        }else {
//
//                            Log.e("LIP_AXIS" , "FACE close")
//                        }

//                        Log.e("LIP_AXIS", "X->" + (top.x - it.x).toString())
//                        Log.e("LIP_AXIS", "Y->" + (top.y - it.y).toString())
                    }
                }
            }

//            getRegionPose(FaceRegion.DOWN_LIP)?.let {
//                downLip?.localPosition = Vector3(it.x, it.y - 0.035f, it.z + 0.015f)
//                downLip?.localScale = Vector3(0.07f, 0.07f, 0.07f)
//            }


        }
    }
}