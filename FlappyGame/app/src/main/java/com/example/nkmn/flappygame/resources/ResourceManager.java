package com.example.nkmn.flappygame.resources;

import java.io.IOException;

import org.andengine.audio.music.Music;
import org.andengine.audio.music.MusicFactory;
import org.andengine.audio.sound.Sound;
import org.andengine.audio.sound.SoundFactory;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.util.debug.Debug;

import android.graphics.Color;

import com.example.nkmn.flappygame.GameActivity;

public class ResourceManager {

	private static final ResourceManager INSTANCE = new ResourceManager();

	public GameActivity mActivity;

	// TextureAtlas & TextureRegion
	private BitmapTextureAtlas mSplashTextureAtlas;
	public ITextureRegion mSplashTextureRegion;

	//
	private BitmapTextureAtlas mAutoParallaxBackgroundTexture;
	public ITextureRegion mParallaxLayerBack;
	public ITextureRegion mParallaxLayerFront;

	private BitmapTextureAtlas mBitmapTextureAtlas;
	public TiledTextureRegion mBirdTextureRegion;
	public TiledTextureRegion mPipeTextureRegion;

	private BitmapTextureAtlas mSubBitmapTextureAtlas;
	public TiledTextureRegion mStateTextureRegion;
	public ITextureRegion mPausedTextureRegion;
	public ITextureRegion mResumedTextureRegion;
	public TiledTextureRegion mButtonTextureRegion;
	public TiledTextureRegion mMedalTextureRegion;

	// font
	public Font mFont1;
	public Font mFont2;
	public Font mMainMenuFont;
	public Font mGameOverFont;
	public Font mFont5;
	
	// sound
	public Sound mSound;
	public Music mMusic;
	
	private ResourceManager() {
	}

	public static ResourceManager getInstance() {
		return INSTANCE;
	}

	public void prepare(GameActivity activity) {
		INSTANCE.mActivity = activity;
	}

	public void loadSplashResources() {
		// TODO implement
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/splash/");
		mSplashTextureAtlas = new BitmapTextureAtlas(
				mActivity.getTextureManager(), 512, 512, TextureOptions.BILINEAR);
		mSplashTextureRegion = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(mSplashTextureAtlas, mActivity, "logo.png", 0, 0);
		mSplashTextureAtlas.load();
		
		// load font
	    FontFactory.setAssetBasePath("font/");
		mFont1 = FontFactory.createFromAsset(mActivity.getFontManager(),
				mActivity.getTextureManager(), 256, 256, mActivity.getAssets(),
				"arial.ttf", 46, true, Color.BLACK);
	    mFont1.load();
	}

	public void unloadSplashResources() {
		// TODO implement
		mSplashTextureAtlas.unload();
		mSplashTextureRegion = null;
		
		mFont1.unload();
	    mFont1 = null;
	}

	
	// ----------------------------------------
	
	// font game
	private void loadGameFonts(){
		
	    FontFactory.setAssetBasePath("font/");
	    mFont2 = FontFactory.createFromAsset(mActivity.getFontManager(), mActivity.getTextureManager(), 256, 256, mActivity.getAssets(), "arial.ttf", 40, true, Color.YELLOW);
	    mFont2.load();
	     
	  
	    mMainMenuFont = FontFactory.createFromAsset(mActivity.getFontManager(), mActivity.getTextureManager(), 256, 256, mActivity.getAssets(), "Plok.ttf", 24, true, Color.WHITE);
	    mMainMenuFont.load();
	     
	    mGameOverFont = FontFactory.createFromAsset(mActivity.getFontManager(), mActivity.getTextureManager(), 256, 256, mActivity.getAssets(), "arial.ttf", 18, true, Color.BLACK);
	    mGameOverFont.load();
	    
	    mFont5 = FontFactory.createFromAsset(mActivity.getFontManager(), mActivity.getTextureManager(), 256, 256, mActivity.getAssets(), "arial.ttf", 36, true, Color.WHITE);
	    mFont5.load(); 
	         
	}
	private void unloadGameFonts(){
		mGameOverFont.unload();
		mGameOverFont = null;      
	     
	    mFont2.unload();
	    mFont2 = null;
	     
	    mMainMenuFont.unload();
	    mMainMenuFont = null;
	     
	    mFont5.unload();
	    mFont5 = null; 
	}
	
	// game graphics
	private void loadGameGraphics(){
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/game/");

		// load background
		mAutoParallaxBackgroundTexture = new BitmapTextureAtlas(
				mActivity.getTextureManager(), 512, 1024);
		mParallaxLayerFront = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(mAutoParallaxBackgroundTexture, mActivity,
						"Flappy_Ground.png", 0, 0);
		mParallaxLayerBack = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(mAutoParallaxBackgroundTexture, mActivity,
						"Flappy_Background.png", 0, 150);
		mAutoParallaxBackgroundTexture.load();

		// load pipe
		mBitmapTextureAtlas = new BitmapTextureAtlas(
				mActivity.getTextureManager(), 128, 512,
				TextureOptions.BILINEAR);
		mBirdTextureRegion = BitmapTextureAtlasTextureRegionFactory
				.createTiledFromAsset(mBitmapTextureAtlas, mActivity,
						"Flappy_Birdies.png", 0, 0, 1, 3);
		mPipeTextureRegion = BitmapTextureAtlasTextureRegionFactory
				.createTiledFromAsset(mBitmapTextureAtlas, mActivity,
						"Flappy_Pipe.png", 0, 125, 2, 1);
		mBitmapTextureAtlas.load();

		// load state game
		mSubBitmapTextureAtlas = new BitmapTextureAtlas(
				mActivity.getTextureManager(), 512, 512,
				TextureOptions.BILINEAR);
		mStateTextureRegion = BitmapTextureAtlasTextureRegionFactory
				.createTiledFromAsset(mSubBitmapTextureAtlas, mActivity,
						"ready_over.png", 0, 0, 2, 1);
		mPausedTextureRegion = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(mSubBitmapTextureAtlas, mActivity,
						"board.png", 0, 60);
		mResumedTextureRegion = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(mSubBitmapTextureAtlas, mActivity, "help.png",
						0, 200);
		mButtonTextureRegion = BitmapTextureAtlasTextureRegionFactory
				.createTiledFromAsset(mSubBitmapTextureAtlas, mActivity,
						"play_pos.png", 0, 350, 2, 1);
		mMedalTextureRegion = BitmapTextureAtlasTextureRegionFactory
				.createTiledFromAsset(mSubBitmapTextureAtlas, mActivity,
						"medal.png", 0, 450, 4, 1);
		mSubBitmapTextureAtlas.load();
	}
	private void unloadGameGraphics(){
		mAutoParallaxBackgroundTexture.unload();
		mBitmapTextureAtlas.unload();
		mSubBitmapTextureAtlas.unload();

		mAutoParallaxBackgroundTexture = null;
		mParallaxLayerFront = null;
		mParallaxLayerBack = null;

		mBitmapTextureAtlas = null;
		mBirdTextureRegion = null;
		mPipeTextureRegion = null;

		mSubBitmapTextureAtlas = null;
		mStateTextureRegion = null;
		mPausedTextureRegion = null;
		mResumedTextureRegion = null;
		mButtonTextureRegion = null;
		mMedalTextureRegion = null;
	}
	
	// game sound
	private void loadGameSounds(){
		SoundFactory.setAssetBasePath("mfx/");
	    try {
	        mSound = SoundFactory.createSoundFromAsset(mActivity.getEngine().getSoundManager(), mActivity, "metal_hit.ogg");
	    } catch (final IOException e) {
	        Debug.e(e);
	    }
	     
	    MusicFactory.setAssetBasePath("mfx/");
	    try {
	        mMusic = MusicFactory.createMusicFromAsset(mActivity.getEngine().getMusicManager(), mActivity, "bird_sound.ogg");
	        mMusic.setLooping(true);
	    } catch (final IOException e) {
	        Debug.e(e);
	    }
	}
	private void unloadGameSounds(){
		mSound.release();
	    mSound = null;
	     
	    mMusic.stop();
	    mMusic.release();
	    mMusic = null;
	}

	// setup resources
	public void loadGameResources() {
		// TODO implement
		loadGameGraphics();
		loadGameFonts();
		loadGameSounds();
	}

	public void unloadGameResources() {
		// TODO implement
		
		unloadGameGraphics();
		unloadGameFonts();
		unloadGameSounds();
	}

}
