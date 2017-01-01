package com.example.nkmn.flappygame.scenes;

import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.opengl.util.GLState;
import org.andengine.util.HorizontalAlign;
import org.andengine.engine.camera.Camera;

import com.example.nkmn.flappygame.scenes.SceneManager.SceneType;

public class SplashScene extends BaseScene {
    
    @Override
    public void createScene() {
 
        Sprite splash = new Sprite(0, 0, mResourceManager.mSplashTextureRegion, mVertexBufferObjectManager) {
            @Override
            protected void preDraw(GLState pGLState, Camera pCamera) 
            {
               super.preDraw(pGLState, pCamera);
               pGLState.enableDither();
            }
        };
        attachChild(splash);
    }
     
    @Override
    public void onBackKeyPressed() {
        mActivity.finish();
    }
 
    @Override
    public SceneType getSceneType() {
        return SceneType.SCENE_SPLASH;
    }
 
    @Override
    public void disposeScene() {
    }
 
}
