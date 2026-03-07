package io.w8.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import io.w8.ucore.noise.Simplex;
import io.w8.ucore.util.Angles;
import io.w8.ucore.util.Mathf;

/** Test sandbox for core game mechanics. */
public class Starter extends ApplicationAdapter {
    private SpriteBatch batch;
    private Texture image;
    private BitmapFont font;

    private final Vector2 shipPosition = new Vector2(220f, 180f);
    private final Vector2 shipVelocity = new Vector2();
    private float shipRotation;
    private float shootCooldown;
    private float elapsed;

    private final Array<Projectile> projectiles = new Array<>();

    private record Projectile(Vector2 position, Vector2 velocity, float ttl) {
        Projectile step(float delta) {
            return new Projectile(position.cpy().mulAdd(velocity, delta), velocity, ttl - delta);
        }
    }

    @Override
    public void create() {
        batch = new SpriteBatch();
        image = new Texture("libgdx.png");
        font = new BitmapFont();
    }

    @Override
    public void render() {
        final float delta = Gdx.graphics.getDeltaTime();
        elapsed += delta;
        updateShip(delta);
        updateProjectiles(delta);

        float bg = 0.08f + Simplex.noise(elapsed * 0.2f, 0f) * 0.06f;
        ScreenUtils.clear(bg, bg + 0.02f, bg + 0.05f, 1f);

        batch.begin();
        drawShip();
        drawProjectiles();
        font.setColor(Color.WHITE);
        font.draw(batch,
                "WASD - move, mouse/arrow - rotate, SPACE - fire | bullets: " + projectiles.size,
                16f,
                Gdx.graphics.getHeight() - 16f);
        batch.end();
    }

    private void updateShip(float delta) {
        float thrust = Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) ? 1020f : 220f;
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            shipVelocity.add(Angles.trnsx(shipRotation, thrust * delta), Angles.trnsy(shipRotation, thrust * delta));
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            shipVelocity.sub(Angles.trnsx(shipRotation, thrust * 0.6f * delta), Angles.trnsy(shipRotation, thrust * 0.6f * delta));
        }

        if (Gdx.input.isKeyPressed(Input.Keys.A)) shipVelocity.rotateDeg(120f * delta);
        if (Gdx.input.isKeyPressed(Input.Keys.D)) shipVelocity.rotateDeg(-120f * delta);

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) shipRotation += 180f * delta;
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) shipRotation -= 180f * delta;

        if (Gdx.input.isTouched()) {
            float tx = Gdx.input.getX();
            float ty = Gdx.graphics.getHeight() - Gdx.input.getY();
            shipRotation = Angles.angle(shipPosition.x, shipPosition.y, tx, ty);
        }

        shipVelocity.scl(Mathf.lerp(1f, 0f, Math.min(1f, delta * 1.8f)));
        shipPosition.mulAdd(shipVelocity, delta);

        shipPosition.x = MathUtils.clamp(shipPosition.x, 0f, Gdx.graphics.getWidth());
        shipPosition.y = MathUtils.clamp(shipPosition.y, 0f, Gdx.graphics.getHeight());

        shootCooldown -= delta;
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE) && shootCooldown <= 0f) {
            fireProjectile();
            shootCooldown = 0.12f;
        }
    }

    private void fireProjectile() {
        Vector2 position = new Vector2(
                shipPosition.x + Angles.trnsx(shipRotation, 24f),
                shipPosition.y + Angles.trnsy(shipRotation, 24f));
        Vector2 velocity = new Vector2(
                Angles.trnsx(shipRotation, 420f),
                Angles.trnsy(shipRotation, 420f)).add(shipVelocity.x, shipVelocity.y);
        projectiles.add(new Projectile(position, velocity, 2.4f));
    }

    private void updateProjectiles(float delta) {
        for (int i = projectiles.size - 1; i >= 0; i--) {
            Projectile next = projectiles.get(i).step(delta);
            if (next.ttl() <= 0f) {
                projectiles.removeIndex(i);
                continue;
            }
            projectiles.set(i, next);
        }
    }

    private void drawShip() {
        float w = 48f;
        float h = 48f;
        batch.setColor(Color.WHITE);
        batch.draw(image, shipPosition.x - w / 2f, shipPosition.y - h / 2f, w / 2f, h / 2f, w, h, 1f, 1f, shipRotation, 0, 0, image.getWidth(), image.getHeight(), false, false);
    }

    private void drawProjectiles() {
        float w = 10f;
        float h = 10f;
        batch.setColor(new Color(1f, 0.9f, 0.4f, 1f));
        for (Projectile projectile : projectiles) {
            batch.draw(image, projectile.position().x - w / 2f, projectile.position().y - h / 2f, w, h);
        }
        batch.setColor(Color.WHITE);
    }

    @Override
    public void dispose() {
        batch.dispose();
        image.dispose();
        font.dispose();
    }
}
