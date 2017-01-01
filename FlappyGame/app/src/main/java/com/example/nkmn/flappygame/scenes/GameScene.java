package com.example.nkmn.flappygame.scenes;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.example.nkmn.flappygame.entity.Pipe;
import com.example.nkmn.flappygame.entity.PipePool;
import com.example.nkmn.flappygame.scenes.SceneManager.SceneType;

import org.andengine.engine.camera.hud.HUD;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.CameraScene;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.AutoParallaxBackground;
import org.andengine.entity.scene.background.ParallaxBackground.ParallaxEntity;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.sprite.TiledSprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.entity.util.FPSLogger;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.extension.physics.box2d.util.Vector2Pool;
import org.andengine.input.touch.TouchEvent;
import org.andengine.util.HorizontalAlign;
import org.andengine.util.color.Color;

public class GameScene extends BaseScene implements IOnSceneTouchListener {

    private AutoParallaxBackground mAutoParallaxBackground;
    private Text mHudText;
    private int score;
    private int most;
    private AnimatedSprite mBird;
    private Pipe mPipe;
    private PipePool mPipePool;

    private boolean mGameOver;
    private float mPipeWidth;

    private PhysicsWorld mPhysicsWorld;

    private CameraScene mGameReadyScene;
    private CameraScene mGameOverScene;
    private Text scoreText;
    private Text mostText;
    private TiledSprite medalSprite;

    private boolean isPlayOverGameSound = false;

    // -----------------------------------------------
    // create background
    private void createBackGround() {
        mAutoParallaxBackground = new AutoParallaxBackground(0, 0, 0, 20);
        mAutoParallaxBackground.attachParallaxEntity(new ParallaxEntity(-1.0f,
                new Sprite(0, SCREEN_HEIGHT
                        - mResourceManager.mParallaxLayerBack.getHeight(),
                        mResourceManager.mParallaxLayerBack,
                        mVertexBufferObjectManager)));


        mAutoParallaxBackground.attachParallaxEntity(new ParallaxEntity(-2.0f, new Sprite(0, SCREEN_HEIGHT
                - mResourceManager.mParallaxLayerFront.getHeight(),
                mResourceManager.mParallaxLayerFront,
                mVertexBufferObjectManager)));
        setBackground(mAutoParallaxBackground);
    }

    // create HUD
    private void createHUD() {
        most = mActivity.getMaxScore();

        // create HUD for score
        HUD gameHUD = new HUD();
        // CREATE SCORE TEXT
        mHudText = new Text(SCREEN_WIDTH / 2, 50, mResourceManager.mFont5,
                "0123456789", new TextOptions(HorizontalAlign.LEFT), mVertexBufferObjectManager);
        mHudText.setText("0");
        mHudText.setX((SCREEN_WIDTH - mHudText.getWidth()) / 2);
        mHudText.setVisible(false);
        gameHUD.attachChild(mHudText);
        mCamera.setHUD(gameHUD);
    }

    // create entity
    private void createEntity() {

        // bird
        final float birdX = (SCREEN_WIDTH - mResourceManager.mBirdTextureRegion.getWidth()) / 2 - 50;
        final float birdY = (SCREEN_HEIGHT - mResourceManager.mBirdTextureRegion.getHeight()) / 2 - 30;

        mBird = new AnimatedSprite(birdX, birdY,
                mResourceManager.mBirdTextureRegion, mVertexBufferObjectManager);
        mBird.setZIndex(10);
        mBird.animate(200);

        attachChild(mBird);

        mPipeWidth = mResourceManager.mPipeTextureRegion.getWidth();

        // create pipe
        final Rectangle ground = new Rectangle(0, SCREEN_HEIGHT
                - mResourceManager.mParallaxLayerFront.getHeight(),
                SCREEN_WIDTH, mResourceManager.mParallaxLayerFront.getHeight(),
                mVertexBufferObjectManager);
        ground.setColor(Color.TRANSPARENT);
        final Rectangle roof = new Rectangle(0, 0, SCREEN_WIDTH, 1, mVertexBufferObjectManager);
        roof.setColor(Color.TRANSPARENT);

        mPipePool = new PipePool(mResourceManager.mPipeTextureRegion, mVertexBufferObjectManager, ground.getY());
        mPipePool.batchAllocatePoolItems(10);
        mPipe = mPipePool.obtainPoolItem();

        attachChild(ground);
        attachChild(roof);
        attachChild(mPipe);

        // TODO create body and fixture
        final FixtureDef wallFixtureDef = PhysicsFactory.createFixtureDef(0, 0, 0);
        final Body groundBody = PhysicsFactory.createBoxBody(mPhysicsWorld, ground, BodyType.StaticBody, wallFixtureDef);
        groundBody.setUserData("ground");

        final FixtureDef birdFixtureDef = PhysicsFactory.createFixtureDef(1, 0, 0);
        final Body birdBody = PhysicsFactory.createCircleBody(mPhysicsWorld, mBird, BodyType.DynamicBody, birdFixtureDef);
        birdBody.setUserData("bird");
        mBird.setUserData(birdBody);

        mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(mBird, birdBody, true, false));
    }

    // create gravity
    private void createPhysicsWorld() {
        mPhysicsWorld = new PhysicsWorld(new Vector2(0, 13.0f), false);
        mPhysicsWorld.setContactListener(createContactListener());
    }

    private ContactListener createContactListener() {
        // TODO Auto-generated method stub
        ContactListener contactListener = new ContactListener() {
            @Override
            public void beginContact(Contact pContact) {
                final Fixture fixtureA = pContact.getFixtureA();
                final Body bodyA = fixtureA.getBody();
                final String userDataA = (String) bodyA.getUserData();

                final Fixture fixtureB = pContact.getFixtureB();
                final Body bodyB = fixtureB.getBody();
                final String userDataB = (String) bodyB.getUserData();

                if (("bird".equals(userDataA) && "ground".equals(userDataB))
                        || ("ground".equals(userDataA) && "bird"
                        .equals(userDataB))) {
                    mGameOver = true;
                    mResourceManager.mMusic.pause();
                    if (!isPlayOverGameSound) {
                        mResourceManager.mGameOverSound.play();
                        isPlayOverGameSound = true;
                    }
                    mBird.stopAnimation(0);
                    mPipe.die();
                    mAutoParallaxBackground.setParallaxChangePerSecond(0);

                    if (score > most) {
                        most = score;
                        mActivity.setMaxScore(most);
                    }

                    mBird.setVisible(false);
                    mHudText.setVisible(false);

                    // TODO display game over with score
                    scoreText.setText(String.valueOf(score));
                    mostText.setText(String.valueOf(most));
                    medalSprite.setCurrentTileIndex(score > 100 ? 3 : (score > 50 ? 2 : (score > 10 ? 1 : 0)));
                    setChildScene(mGameOverScene, false, true, true);
                }
            }

            @Override
            public void endContact(Contact contact) {
            }

            @Override
            public void postSolve(Contact contact, ContactImpulse impulse) {
            }

            @Override
            public void preSolve(Contact contact, Manifold oldManifold) {
                // TODO Auto-generated method stub

            }

        };
        return contactListener;
    }


    // ---------------SubScene---------------------------
    // Game ready
    private void createSubScene() {

        final float labelX = (SCREEN_WIDTH - mResourceManager.mStateTextureRegion.getWidth()) / 2;
        final float labelY = 100;
        //create CameraScene for game over
        final float overX = (SCREEN_WIDTH - mResourceManager.mPausedTextureRegion.getWidth()) / 2;
        final float overY = labelY + mResourceManager.mStateTextureRegion.getHeight();

        final float playX = overX;
        final float playY = overY + mResourceManager.mPausedTextureRegion.getHeight();

        final float posX = SCREEN_WIDTH / 2;
        final float posY = playY;

        final float medalX = overX + 46;
        final float medalY = overY + 46;

        final float scoreX = overX + 165;
        final float scoreY = overY + 40;

        final float mostX = scoreX;
        final float mostY = scoreY + 25;

        mGameOverScene = new CameraScene(mCamera);

        final TiledSprite labelSprite = new TiledSprite(labelX, labelY, mResourceManager.mStateTextureRegion, mVertexBufferObjectManager);
        labelSprite.setCurrentTileIndex(1);
        mGameOverScene.attachChild(labelSprite);

        final Sprite pauseSprite = new Sprite(overX, overY, mResourceManager.mPausedTextureRegion, mVertexBufferObjectManager);
        pauseSprite.setScale(0.75f);
        mGameOverScene.attachChild(pauseSprite);

        medalSprite = new TiledSprite(medalX, medalY, mResourceManager.mMedalTextureRegion, mVertexBufferObjectManager);
        medalSprite.setCurrentTileIndex(0);
        medalSprite.setScale(0.75f);
        mGameOverScene.attachChild(medalSprite);

        scoreText = new Text(scoreX, scoreY, mResourceManager.mGameOverFont, "0123456789", new TextOptions(HorizontalAlign.LEFT), mVertexBufferObjectManager);
        scoreText.setText("0");
        mGameOverScene.attachChild(scoreText);

        mostText = new Text(mostX, mostY, mResourceManager.mGameOverFont, "0123456789", new TextOptions(HorizontalAlign.LEFT), mVertexBufferObjectManager);
        mostText.setText(String.valueOf(most));
        mGameOverScene.attachChild(mostText);

        final TiledSprite playSprite = new TiledSprite(playX, playY, mResourceManager.mButtonTextureRegion, mVertexBufferObjectManager) {

            @Override
            public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
                if (pSceneTouchEvent.isActionUp()) {
                    clearChildScene();
                    mResourceManager.mGameOverSound.stop();
                    if (!mResourceManager.mMusic.isPlaying())
                        mResourceManager.mMusic.play();
                    mSceneManager.setScene(SceneType.SCENE_GAME);
                }
                return true;
            }
        };
        playSprite.setCurrentTileIndex(0);
        playSprite.setScale(0.75f);
        mGameOverScene.registerTouchArea(playSprite);
        mGameOverScene.attachChild(playSprite);

        final TiledSprite posSprite = new TiledSprite(posX, posY, mResourceManager.mButtonTextureRegion, mVertexBufferObjectManager);
        posSprite.setCurrentTileIndex(1);
        posSprite.setScale(0.75f);
        mGameOverScene.registerTouchArea(posSprite);
        mGameOverScene.attachChild(posSprite);

        mGameOverScene.setBackgroundEnabled(false);


        // create Game ready scene
        final float readyX = (SCREEN_WIDTH - mResourceManager.mResumedTextureRegion.getWidth()) / 2;
        final float readyY = 200;

        mGameReadyScene = new CameraScene(mCamera);
        final TiledSprite label2Sprite = new TiledSprite(labelX, labelY, mResourceManager.mStateTextureRegion, mVertexBufferObjectManager);
        label2Sprite.setCurrentTileIndex(0);
        mGameReadyScene.attachChild(label2Sprite);
        final Sprite resumedSprite = new Sprite(readyX, readyY, mResourceManager.mResumedTextureRegion, mVertexBufferObjectManager);
        mGameReadyScene.attachChild(resumedSprite);

        mGameReadyScene.setBackgroundEnabled(false);

        mGameReadyScene.setOnSceneTouchListener(new IOnSceneTouchListener() {

            @Override
            public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
                if (pSceneTouchEvent.isActionUp()) {
                    clearChildScene();
                    mHudText.setVisible(true);
                }
                return true;
            }
        });

        setChildScene(mGameReadyScene, false, true, true);
    }

    @Override
    public void createScene() {
        mEngine.registerUpdateHandler(new FPSLogger());

        setOnSceneTouchListener(this);

        createBackGround();
        // display score
        createHUD();

        // TODO create PhysicsWorld
        createPhysicsWorld();

        // TODO create entities
        createEntity();

        // create subscene
        createSubScene();
        /* The actual collision-checking. */

        registerUpdateHandler(new IUpdateHandler() {

            @Override
            public void reset() {
            }

            @Override
            public void onUpdate(float pSecondsElapsed) {

                // bird - flying direction
                Vector2 veclocity = ((Body) mBird.getUserData()).getLinearVelocity();
                if (veclocity.y < 0)
                    mBird.setRotation(-15);
                else if (veclocity.y == 0)
                    mBird.setRotation(0);
                else
                    mBird.setRotation(15);
                // end

                if (!mGameOver && mPipe.collidesWith(mBird)) {
                    mGameOver = true;
                    mResourceManager.mMusic.pause();
                    if (!isPlayOverGameSound) {
                        mResourceManager.mGameOverSound.play();
                        isPlayOverGameSound = true;
                    }
                    mResourceManager.mSound.play();
                    mBird.stopAnimation(0);
                    mPipe.die();
                    mAutoParallaxBackground.setParallaxChangePerSecond(0);
                    return;
                }

                if (mPipe.getX() < -mPipeWidth) {
                    detachChild(mPipe);
                    mPipePool.recyclePoolItem(mPipe);
                    mPipePool.shufflePoolItems();

                    mPipe = mPipePool.obtainPoolItem();
                    attachChild(mPipe);
                    sortChildren();
                }

                if (score != mPipePool.getPipeIndex()
                        && mBird.getX() > (mPipe.getX() + mPipeWidth)) {
                    score = mPipePool.getPipeIndex();
                    mHudText.setText(String.valueOf(score));
                }
                mPhysicsWorld.onUpdate(pSecondsElapsed);
            }
        });


        // TODO create CameraScene for 'get ready'

        // TODO create CameraScene for 'game over'

        // TODO create HUD for score
    }

    @Override
    public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
        // TODO implement
        if (mPhysicsWorld != null) {
            if (!mGameOver && pSceneTouchEvent.isActionDown()) {
                jumpFace(mBird);
                return true;
            }
        }
        return false;
    }

    private void jumpFace(final AnimatedSprite face) {
        // TODO implement
        final Body faceBody = (Body) face.getUserData();
        final Vector2 velocity = Vector2Pool.obtain(0, -4);
        faceBody.setLinearVelocity(velocity);
        Vector2Pool.recycle(velocity);
        mResourceManager.mFlappySound.play();
    }

    @Override
    public void onBackKeyPressed() {
        mSceneManager.setScene(SceneType.SCENE_MENU);
    }

    @Override
    public SceneType getSceneType() {
        return SceneType.SCENE_GAME;
    }

    @Override
    public void disposeScene() {
        // TODO
    }

}
