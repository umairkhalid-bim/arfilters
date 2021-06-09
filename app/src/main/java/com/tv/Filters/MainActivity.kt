package com.tv.Filters


import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.ar.core.AugmentedFace
import com.google.ar.core.TrackingState
import com.google.ar.sceneform.Sceneform
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.rendering.Renderable
import com.google.ar.sceneform.rendering.RenderableInstance
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.ArFragment.OnTapModelListener
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File


class MainActivity : AppCompatActivity() {
    private lateinit var downloader: FbFIleDownloader
    private var faceRegionsRenderable: ModelRenderable? = null

    var faceNodeMap = HashMap<AugmentedFace, CustomFaceNode>()
    private var changeModel: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        downloader = FbFIleDownloader()
        downloader?.download(onSuccess = {
            buildModel(it)
        }, onProgress = {
//            this.progress(it)
        }, onFailure = {
//            this.errorAya(it)
        })


        val sceneView = (arFragment as FaceArFragment).arSceneView
//        sceneView.cameraStreamRenderPriority = Renderable.RENDER_PRIORITY_FIRST
        val scene = sceneView.scene

        scene.addOnUpdateListener {
            if (faceRegionsRenderable != null) {
                sceneView.session
                    ?.getAllTrackables(AugmentedFace::class.java)?.let {
                        for (f in it) {
                            if (!faceNodeMap.containsKey(f)) {
                                val faceNode = CustomFaceNode(f, this, onMouth = {
                                    playMouthOpen(it)
                                })
                                faceNode.setParent(scene)
                                faceNode.faceRegionsRenderable = faceRegionsRenderable
                                faceNodeMap.put(f, faceNode)
                            }else if (changeModel) {
                                faceNodeMap.getValue(f).faceRegionsRenderable = faceRegionsRenderable
                            }
                        }

                        changeModel = false
                        // Remove any AugmentedFaceNodes associated with an AugmentedFace that stopped tracking.
                        val iter = faceNodeMap.entries.iterator()
                        while (iter.hasNext()) {
                            val entry = iter.next()
                            val face = entry.key
                            if (face.trackingState == TrackingState.STOPPED) {
                                val faceNode = entry.value
                                faceNode.setParent(null)
                                iter.remove()
                            }
                        }
                    }
            }
        }


//        supportFragmentManager.addFragmentOnAttachListener { fm : FragmentManager, fragment : Fragment ->
//            when(fragment.id) {
//                R.id.arFragment -> {
//                    val arFragment = fragment as FaceArFragment
//
//                    val sceneView = arFragment.arSceneView
//                    sceneView.cameraStreamRenderPriority = Renderable.RENDER_PRIORITY_FIRST
//                    val scene = sceneView.scene
//
////                    ModelRenderable.builder().setSource(
////
////                    )
////
//////                    ModelRenderable.builder
//////                        .setSource(
//////                            getContext(),
//////                            Uri.parse(glbSource)
//////                        )
//////                        .setIsFilamentGltf(true)
//////                        .build()
//////                        .thenAccept { modelRenderable: ModelRenderable ->
//////                            onTapRenderable = modelRenderable
//////                        }
//////                        .exceptionally { throwable: Throwable? ->
//////                            if (listener != null) {
//////                                listener.onModelError(throwable)
//////                            }
//////                            null
//////                        }
////
////                    arFragment.setOnTapPlaneGlbModel("model.glb", object : OnTapModelListener {
////                        override fun onModelAdded(renderableInstance: RenderableInstance) {
////
////                        }
////                        override fun onModelError(exception: Throwable) {
////
////                        }
////                    })
//                }
//            }
//        }

        if (savedInstanceState == null) {
            if (Sceneform.isSupported(this)) {
                supportFragmentManager.beginTransaction()
                    .add(R.id.arFragment, ArFragment::class.java, null)
                    .commit()
            }
        }
    }

    private fun buildModel(it: File) {

    }
}