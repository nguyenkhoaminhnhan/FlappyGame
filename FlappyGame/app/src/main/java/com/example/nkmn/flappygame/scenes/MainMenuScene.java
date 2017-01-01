package com.example.nkmn.flappygame.scenes;

import org.andengine.entity.scene.background.AutoParallaxBackground;
import org.andengine.entity.scene.background.ParallaxBackground.ParallaxEntity;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.TextMenuItem;
import org.andengine.entity.scene.menu.item.decorator.ColorMenuItemDecorator;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.sprite.TiledSprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.util.HorizontalAlign;
import org.andengine.util.color.Color;

import android.opengl.GLES20;

import com.example.nkmn.flappygame.scenes.SceneManager.SceneType;

public class MainMenuScene extends BaseScene implements IOnMenuItemClickListener {
    
    protected MenuScene mMenuScene;
 
    protected static final int MENU_PLAY = 0;
    protected static final int MENU_EXTRAS = 2;
    protected static final int MENU_QUIT = 3; 
    
    @Override
    public void createScene() {
        AutoParallaxBackground autoParallaxBackground = new AutoParallaxBackground(0, 0, 0, 0);
        autoParallaxBackground.attachParallaxEntity(new ParallaxEntity(0.0f, new Sprite(0, SCREEN_HEIGHT - mResourceManager.mParallaxLayerBack.getHeight(), mResourceManager.mParallaxLayerBack, mVertexBufferObjectManager)));
        autoParallaxBackground.attachParallaxEntity(new ParallaxEntity(0.0f, new Sprite(0, SCREEN_HEIGHT - mResourceManager.mParallaxLayerFront.getHeight(), mResourceManager.mParallaxLayerFront, mVertexBufferObjectManager)));
        setBackground(autoParallaxBackground);
         
        Text nameText = new Text(0, 0, mResourceManager.mFont2, "Flappy Game", new TextOptions(HorizontalAlign.LEFT), mVertexBufferObjectManager);
        nameText.setPosition((SCREEN_WIDTH - nameText.getWidth())/2f, 75);
        attachChild(nameText);
         
        final float birdX = (SCREEN_WIDTH - mResourceManager.mBirdTextureRegion.getWidth()) / 2;
        final float birdY = nameText.getY() + nameText.getHeight() + 25;
        TiledSprite bird = new TiledSprite(birdX, birdY, mResourceManager.mBirdTextureRegion, mVertexBufferObjectManager);
        bird.setRotation(-15);
        attachChild(bird);
         
        mMenuScene = createMenuScene();
         
        /* Attach the menu. */
        this.setChildScene(mMenuScene, false, true, true);
         
        if (!mResourceManager.mMusic.isPlaying()) {
            mResourceManager.mMusic.play();
        }
    }
     
    @Override
    public boolean onMenuItemClicked(final MenuScene pMenuScene, final IMenuItem pMenuItem, final float pMenuItemLocalX, final float pMenuItemLocalY) {
        //TODO implement
    	switch(pMenuItem.getID()) {
        case MENU_PLAY:
            if (mResourceManager.mMusic.isPlaying()) {
                mResourceManager.mMusic.pause();
            }
            mMenuScene.closeMenuScene();
            mSceneManager.setScene(SceneType.SCENE_GAME);
            return true;
             
        case MENU_EXTRAS:
            return true;                
             
        case MENU_QUIT:
            /* End Activity. */
            mActivity.finish();
            return true;                
             
        default:
            return false;
    	}
    }   
     
    protected MenuScene createMenuScene() {
        //TODO implement
    	final MenuScene menuScene = new MenuScene(mCamera);
    	 
        final IMenuItem playMenuItem = new ColorMenuItemDecorator(new TextMenuItem(MENU_PLAY, mResourceManager.mMainMenuFont, "Play", mVertexBufferObjectManager), new Color(1,1,1), new Color(0.0f, 0.2f, 0.4f));
        playMenuItem.setBlendFunction(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        menuScene.addMenuItem(playMenuItem);
     
         
        final IMenuItem extrasMenuItem = new ColorMenuItemDecorator(new TextMenuItem(MENU_EXTRAS, mResourceManager.mMainMenuFont, "Extras", mVertexBufferObjectManager), new Color(1,1,1), new Color(0.0f, 0.2f, 0.4f));
        extrasMenuItem.setBlendFunction(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        menuScene.addMenuItem(extrasMenuItem);
     
        final IMenuItem quitMenuItem = new ColorMenuItemDecorator(new TextMenuItem(MENU_QUIT, mResourceManager.mMainMenuFont, "Quit", mVertexBufferObjectManager), new Color(1,1,1), new Color(0.0f, 0.2f, 0.4f));
        quitMenuItem.setBlendFunction(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        menuScene.addMenuItem(quitMenuItem);        
     
        menuScene.buildAnimations();
     
        menuScene.setBackgroundEnabled(false);
     
        menuScene.setOnMenuItemClickListener(this);
        return menuScene;
    }
     
    protected MenuScene createSubMenuScene() {
        //TODO implement
        return null;
    }   
     
    @Override
    public void onBackKeyPressed() {
    
    	mActivity.finish();
    }
 
    @Override
    public SceneType getSceneType() {
        return SceneType.SCENE_MENU;
    }
 
    @Override
    public void disposeScene() {
        //TODO
    }
     
}
  
