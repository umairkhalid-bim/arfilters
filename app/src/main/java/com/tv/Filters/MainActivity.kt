package com.tv.Filters


import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.ar.core.AugmentedFace
import com.google.ar.core.TrackingState
import com.google.ar.sceneform.Sceneform
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.ux.ArFragment
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.lang.ref.WeakReference


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

    private fun playMouthOpen(it: Boolean) {

    }

    private fun buildModel(it: File) {
        val weakActivity = WeakReference(this)
        ModelRenderable.builder()
            .setSource(
                this,
                Uri.fromFile(it)
//                Uri.parse("https://storage.googleapis.com/ar-answers-in-search-models/static/Tiger/model.glb")
            )
            .setIsFilamentGltf(true)
            .setAsyncLoadEnabled(true)
            .build()
            .thenAccept { model: ModelRenderable ->
                val activity = weakActivity.get()
                if (activity != null) {
                    activity.faceRegionsRenderable = model
                }
            }
            .exceptionally { throwable: Throwable? ->
                Toast.makeText(this, "Unable to load model", Toast.LENGTH_LONG).show()
                null
            }
//        ViewRenderable.builder()
//            .setView(this, R.layout.view_tiger_card)
//            .build()
//            .thenAccept(Consumer { viewRenderable: ViewRenderable ->
//                val activity = weakActivity.get()
//                if (activity != null) {
//                    activity.viewRenderable = viewRenderable
//                }
//            })
//            .exceptionally(Function<Throwable, Void?> { throwable: Throwable? ->
//                Toast.makeText(this, "Unable to load model", Toast.LENGTH_LONG).show()
//                null
//            })
    }
}