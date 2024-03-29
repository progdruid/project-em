package com.projectem.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import com.projectem.game.ecs.*;
import com.projectem.game.input.*;
import com.projectem.game.menu.ISceneManager;
import com.projectem.game.render.Renderer;
import com.projectem.game.ui.*;

public class Game implements IUIAcceptor, ICommonInputAcceptor, Disposable {

    private final IUIManager uiManager;
    private final IPlatformInput inputManager;
    private final ISceneManager sceneManager;

    private OrthographicCamera camera;
    private final float cameraSpeed = 0.25f;
    private final float minZoom = 0.5f;
    private final float maxZoom = 10f;

    public Game (IUIManagerCreator uiCreator, IPlatformInputCreator inputCreator, ISceneManager sceneManager) {
        this.uiManager = uiCreator.createUIManager(this);
        this.inputManager = inputCreator.createInputManager(this);
        this.sceneManager = sceneManager;
    }


    public void start () {
        EntityGod.init();

        TransformSystem.init();
        SpriteSystem.init();
        CellSystem.init();

        inputManager.startProcessing();

        this.camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Renderer.ins.setCamera(camera);

        Entity cell = new Entity("Cell");
        TransformSystem.ins.createComponent(cell);
        SpriteComponent spriteComponent = (SpriteComponent) SpriteSystem.ins.createComponent(cell);
        spriteComponent.setTexture(new Texture(Gdx.files.internal("emptycell.png")));
        CellSystem.ins.createComponent(cell);

    }

    public void logicUpdate () {
        TransformSystem.ins.update();
        CellSystem.ins.update();
    }

    public void frameUpdate() {
        inputManager.update();

        SpriteSystem.ins.update();
    }

    @Override
    public void move(Vector2 direction) {
        move(direction, cameraSpeed);
    }

    @Override
    public void move(Vector2 direction, float speed) {
        camera.translate(direction.scl((camera.zoom + 1) * camera.viewportWidth * speed * Gdx.graphics.getDeltaTime()));
        camera.update();
    }

    @Override
    public void zoom(float amount) {
        float nextZoom = camera.zoom + amount * (camera.zoom + 1);
        if (nextZoom > minZoom && nextZoom < maxZoom)
        {
            camera.zoom = nextZoom;
            camera.update();
        }
    }

    @Override
    public void dispose() {
        EntityGod.ins.dispose();

        CellSystem.ins.dispose();
        SpriteSystem.ins.dispose();
        TransformSystem.ins.dispose();

        Renderer.ins.setCamera(null);
    }

    @Override
    public void quitToMenu() {
        dispose();
        sceneManager.openMainMenu();
    }
}
