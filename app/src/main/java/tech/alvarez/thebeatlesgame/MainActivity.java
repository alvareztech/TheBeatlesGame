package tech.alvarez.thebeatlesgame;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.MotionEvent;
import android.widget.Toast;

import org.anddev.andengine.audio.music.Music;
import org.anddev.andengine.audio.music.MusicFactory;
import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.anddev.andengine.entity.modifier.LoopEntityModifier;
import org.anddev.andengine.entity.modifier.PathModifier;
import org.anddev.andengine.entity.modifier.PathModifier.Path;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.sprite.AnimatedSprite;
import org.anddev.andengine.entity.text.ChangeableText;
import org.anddev.andengine.entity.text.Text;
import org.anddev.andengine.entity.util.FPSLogger;
import org.anddev.andengine.input.touch.TouchEvent;
import org.anddev.andengine.opengl.font.Font;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.anddev.andengine.opengl.texture.region.TiledTextureRegion;
import org.anddev.andengine.ui.activity.BaseGameActivity;
import org.anddev.andengine.util.Debug;
import org.anddev.andengine.util.HorizontalAlign;
import org.anddev.andengine.util.modifier.ease.EaseSineInOut;

import java.io.IOException;

public class MainActivity extends BaseGameActivity {

    public static final int IZQUIERDA = 1;
    public static final int DERECHA = 2;

    public static final int JOHN = 0;
    public static final int PAUL = 1;
    public static final int GEORGE = 2;
    public static final int RINGO = 3;

    // utils
    private boolean jhonSelected = false;
    private boolean paulSelected = false;
    private boolean georgeSelected = false;
    private boolean ringoSelected = false;
    private int jhonLado = IZQUIERDA;
    private int paulLado = IZQUIERDA;
    private int georgeLado = IZQUIERDA;
    private int ringoLado = IZQUIERDA;

    // Constantes
    private static final int CAMARA_ANCHO = 720;
    private static final int CAMARA_ALTO = 480;
    private int VELOCIDAD = 2; // Mas pequenio = mas rapido
    // Objetos
    private Camera camara;
    private BitmapTextureAtlas mapa;
    private BitmapTextureAtlas mapaFont;
    private TiledTextureRegion texturaBotonIzquierda;
    private TiledTextureRegion texturaBotonDerecha;
    private TiledTextureRegion texturaJohn;
    private TiledTextureRegion texturaPaul;
    private TiledTextureRegion texturaGeorge;
    private TiledTextureRegion texturaRingo;

    private Font mFont;

    // Sprites
    private AnimatedSprite spriteBotonIzquierda;
    private AnimatedSprite spriteBotonDerecha;
    private AnimatedSprite[] sprites = new AnimatedSprite[4];

    // Posiciones
    int inicialJohnX = 50;
    int inicialJohnY = 80;
    int finalJohnX = 630;
    int finalJohnY = 80;

    int inicialPaulX = inicialJohnX;
    int inicialPaulY = inicialJohnY * 2;
    int finalPaulX = finalJohnX;
    int finalPaulY = inicialJohnY * 2;

    int inicialGeorgeX = inicialJohnX;
    int inicialGeorgeY = inicialJohnY * 3;
    int finalGeorgeX = finalJohnX;
    int finalGeorgeY = inicialGeorgeY;

    int inicialRingoX = inicialJohnX;
    int inicialRingoY = inicialJohnY * 4;
    int finalRingoX = finalJohnX;
    int finalRingoY = inicialRingoY;

    ChangeableText puntaje;

    private Music mMusic;

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//    }

    @Override
    public Engine onLoadEngine() {
        camara = new Camera(0, 0, CAMARA_ANCHO, CAMARA_ALTO);
        return new Engine(new EngineOptions(true, EngineOptions.ScreenOrientation.LANDSCAPE, new FillResolutionPolicy(), camara).setNeedsMusic(true));
    }

    @Override
    public void onLoadResources() {
        mapaFont = new BitmapTextureAtlas(1024, 1024, TextureOptions.BILINEAR_PREMULTIPLYALPHA);

        mFont = new Font(mapaFont, Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 32, true, Color.WHITE);

        mEngine.getTextureManager().loadTexture(mapaFont);
        mEngine.getFontManager().loadFont(mFont);

        BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
        mapa = new BitmapTextureAtlas(512, 512, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        texturaBotonIzquierda = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(mapa, this, "flecha_izquierda.png", 0, 0, 3, 1);
        texturaBotonDerecha = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(mapa, this, "flecha_derecha.png", 0, 32, 3, 1);
        texturaJohn = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(mapa, this, "john.png", 0, 64, 3, 4);
        texturaPaul = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(mapa, this, "paul.png", 96, 64, 3, 4);
        texturaGeorge = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(mapa, this, "george.png", 192, 64, 3, 4);
        texturaRingo = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(mapa, this, "ringo.png", 288, 64, 3, 4);

        MusicFactory.setAssetBasePath("mfx/");
        try {
            this.mMusic = MusicFactory.createMusicFromAsset(this.mEngine.getMusicManager(), this, "beatlessong.mp3");
            this.mMusic.setLooping(true);
        } catch (final IOException e) {
            Debug.e(e);
        }

        mEngine.getTextureManager().loadTexture(mapa);
    }

    @Override
    public Scene onLoadScene() {
        mEngine.registerUpdateHandler(new FPSLogger());
        final Scene scene = new Scene();
        // ----------------------------------------------------------------------------------------------------------
        // JOHN
        sprites[JOHN] = new AnimatedSprite(inicialJohnX, inicialJohnY, texturaJohn) {
            @Override
            public boolean onAreaTouched(final TouchEvent eventoTouch, final float touchX, final float touchY) {
                if (eventoTouch.getAction() == MotionEvent.ACTION_DOWN) {
                    if (jhonLado == IZQUIERDA) {
                        if (jhonSelected) {
                            sprites[JOHN].setCurrentTileIndex(1);
                            jhonSelected = false;
                        } else {
                            if (!estanDosSeleccionados()) {
                                sprites[JOHN].setCurrentTileIndex(7); // perfil
                                jhonSelected = true;
                            } else {
                                mostrarToast("Ya estan seleccionados 2");
                            }
                        }
                    } else {
                        if (jhonSelected) {
                            sprites[JOHN].setCurrentTileIndex(1);
                            jhonSelected = false;
                        } else {
                            if (!estaUnoSeleccionado()) {
                                sprites[JOHN].setCurrentTileIndex(4);
                                jhonSelected = true;
                            } else {
                                mostrarToast("Ya esta seleccionado uno");
                            }
                        }
                    }
                }
                return true;
            }
        };

        // ----------------------------------------------------------------------------------------------------------
        // PAUL
        sprites[PAUL] = new AnimatedSprite(inicialPaulX, inicialPaulY, texturaPaul) {
            @Override
            public boolean onAreaTouched(final TouchEvent eventoTouch, final float touchX, final float touchY) {
                if (eventoTouch.getAction() == MotionEvent.ACTION_DOWN) {

                    if (paulLado == IZQUIERDA) {

                        if (paulSelected) {
                            sprites[PAUL].setCurrentTileIndex(1);
                            paulSelected = false;
                        } else {
                            if (!estanDosSeleccionados()) {
                                sprites[PAUL].setCurrentTileIndex(7);
                                paulSelected = true;
                            } else {
                                mostrarToast("Ya estan seleccionados 2");
                            }
                        }
                    } else {

                        if (paulSelected) {
                            sprites[PAUL].setCurrentTileIndex(1);
                            paulSelected = false;
                        } else {
                            if (!estaUnoSeleccionado()) {
                                sprites[PAUL].setCurrentTileIndex(4);
                                paulSelected = true;
                            } else {
                                mostrarToast("Ya esta seleccionado uno");
                            }
                        }
                    }

                }
                return true;
            }
        };
        // ----------------------------------------------------------------------------------------------------------
        // GEORGE
        sprites[GEORGE] = new AnimatedSprite(inicialGeorgeX, inicialGeorgeY, texturaGeorge) {
            @Override
            public boolean onAreaTouched(final TouchEvent eventoTouch, final float touchX, final float touchY) {
                if (eventoTouch.getAction() == MotionEvent.ACTION_DOWN) {

                    if (georgeLado == IZQUIERDA) {

                        if (georgeSelected) {
                            sprites[GEORGE].setCurrentTileIndex(1);
                            georgeSelected = false;
                        } else {
                            if (!estanDosSeleccionados()) {
                                sprites[GEORGE].setCurrentTileIndex(7);
                                georgeSelected = true;
                            } else {
                                mostrarToast("Ya estan seleccionados 2");
                            }
                        }
                    } else {
                        if (georgeSelected) {
                            sprites[GEORGE].setCurrentTileIndex(1);
                            georgeSelected = false;
                        } else {
                            if (!estaUnoSeleccionado()) {
                                georgeSelected = true;
                                sprites[GEORGE].setCurrentTileIndex(4);
                            } else {
                                mostrarToast("Ya esta seleccionado uno");
                            }
                        }
                    }

                }
                return true;
            }
        };
        // ----------------------------------------------------------------------------------------------------------
        // RINGO

        sprites[RINGO] = new AnimatedSprite(inicialRingoX, inicialRingoY, texturaRingo) {
            @Override
            public boolean onAreaTouched(final TouchEvent eventoTouch, final float touchX, final float touchY) {
                if (eventoTouch.getAction() == MotionEvent.ACTION_DOWN) {

                    if (ringoLado == IZQUIERDA) {

                        if (ringoSelected) {
                            sprites[RINGO].setCurrentTileIndex(1);
                            ringoSelected = false;
                        } else {
                            if (!estanDosSeleccionados()) {
                                sprites[RINGO].setCurrentTileIndex(7);
                                ringoSelected = true;
                            } else {
                                mostrarToast("Ya estan seleccionados 2");
                            }
                        }
                    } else {

                        if (ringoSelected) {
                            sprites[RINGO].setCurrentTileIndex(1);
                            ringoSelected = false;
                        } else {
                            if (!estaUnoSeleccionado()) {
                                sprites[RINGO].setCurrentTileIndex(4);
                                ringoSelected = true;
                            } else {
                                mostrarToast("Ya esta seleccionado uno");
                            }
                        }
                    }
                }
                return true;
            }
        };

        for (int i = 0; i < sprites.length; i++) {
            sprites[i].setCurrentTileIndex(1);
            sprites[i].setScale(2);
        }
        // Botones
        // ---------------------------------------------------------------------------------------------
        spriteBotonIzquierda = new AnimatedSprite(300, 400, texturaBotonIzquierda) {
            @Override
            public boolean onAreaTouched(final TouchEvent eventoTouch, final float touchX, final float touchY) {
                if (eventoTouch.getAction() == MotionEvent.ACTION_DOWN && spriteBotonIzquierda.getCurrentTileIndex() != 2 && estaUnoSeleccionado()) {
                    spriteBotonIzquierda.setCurrentTileIndex(2);
                    spriteBotonDerecha.setCurrentTileIndex(2);
                    if (jhonSelected) {
                        moveJohnIzquierda();
                        puntaje.setText((Integer.parseInt(puntaje.getText()) + 1) + "");
                    }
                    if (paulSelected) {
                        movePaulIzquierda();
                        puntaje.setText((Integer.parseInt(puntaje.getText()) + 2) + "");
                    }
                    if (georgeSelected) {
                        moveGeorgeIzquierda();
                        puntaje.setText((Integer.parseInt(puntaje.getText()) + 5) + "");
                    }
                    if (ringoSelected) {
                        moveRingoIzquierda();
                        puntaje.setText((Integer.parseInt(puntaje.getText()) + 10) + "");
                    }
                    jhonSelected = false;
                    paulSelected = false;
                    georgeSelected = false;
                    ringoSelected = false;
                }
                return true;
            }
        };
        spriteBotonDerecha = new AnimatedSprite(400, 400, texturaBotonDerecha) {
            @Override
            public boolean onAreaTouched(final TouchEvent eventoTouch, final float touchX, final float touchY) {
                if (eventoTouch.getAction() == MotionEvent.ACTION_DOWN && spriteBotonDerecha.getCurrentTileIndex() != 0 && estanDosSeleccionados()) {
                    spriteBotonIzquierda.setCurrentTileIndex(0);
                    spriteBotonDerecha.setCurrentTileIndex(0);
                    if (jhonSelected && paulSelected && !georgeSelected && !ringoSelected) {
                        moveJohnDerecha();
                        movePaulDerecha2();
                        puntaje.setText((Integer.parseInt(puntaje.getText()) + 2) + "");
                    }
                    if (jhonSelected && !paulSelected && georgeSelected && !ringoSelected) {
                        moveJohnDerecha();
                        moveGeorgeDerecha2();
                        puntaje.setText((Integer.parseInt(puntaje.getText()) + 5) + "");
                    }
                    if (jhonSelected && !paulSelected && !georgeSelected && ringoSelected) {
                        moveJohnDerecha();
                        moveRingoDerecha();
                        puntaje.setText((Integer.parseInt(puntaje.getText()) + 10) + "");
                    }
                    if (!jhonSelected && paulSelected && georgeSelected && !ringoSelected) {
                        movePaulDerecha1();
                        moveGeorgeDerecha2();
                        puntaje.setText((Integer.parseInt(puntaje.getText()) + 5) + "");
                    }
                    if (!jhonSelected && paulSelected && !georgeSelected && ringoSelected) {
                        movePaulDerecha1();
                        moveRingoDerecha();
                        puntaje.setText((Integer.parseInt(puntaje.getText()) + 10) + "");
                    }
                    if (!jhonSelected && !paulSelected && georgeSelected && ringoSelected) {
                        moveGeorgeDerecha1();
                        moveRingoDerecha();
                        puntaje.setText((Integer.parseInt(puntaje.getText()) + 10) + "");
                    }
                    jhonSelected = false;
                    paulSelected = false;
                    georgeSelected = false;
                    ringoSelected = false;
                    checkFinish();
                }
                return true;
            }

        };

        spriteBotonIzquierda.setCurrentTileIndex(2);
        spriteBotonDerecha.setCurrentTileIndex(2);
        spriteBotonIzquierda.setScale(2);
        spriteBotonDerecha.setScale(2);

        scene.attachChild(sprites[JOHN]);
        scene.attachChild(sprites[PAUL]);
        scene.attachChild(sprites[GEORGE]);
        scene.attachChild(sprites[RINGO]);
        scene.attachChild(spriteBotonIzquierda);
        scene.attachChild(spriteBotonDerecha);
        // Botones
        scene.registerTouchArea(spriteBotonIzquierda);
        scene.registerTouchArea(spriteBotonDerecha);
        // Personas
        scene.registerTouchArea(sprites[JOHN]);
        scene.registerTouchArea(sprites[PAUL]);
        scene.registerTouchArea(sprites[GEORGE]);
        scene.registerTouchArea(sprites[RINGO]);
        // Textos

        final Text uno = new Text(15, 80, mFont, "1", HorizontalAlign.CENTER);
        final Text dos = new Text(15, 160, mFont, "2", HorizontalAlign.CENTER);
        final Text cinco = new Text(15, 240, mFont, "5", HorizontalAlign.CENTER);
        final Text diez = new Text(5, 320, mFont, "10", HorizontalAlign.CENTER);

        final Text puntajeTitulo = new Text(240, 15, mFont, getString(R.string.movements), HorizontalAlign.CENTER);
        puntaje = new ChangeableText(420, 15, mFont, "0", 3);

        scene.attachChild(uno);
        scene.attachChild(dos);
        scene.attachChild(cinco);
        scene.attachChild(diez);
        scene.attachChild(puntajeTitulo);
        scene.attachChild(puntaje);

        if (MainActivity.this.mMusic.isPlaying()) {
            MainActivity.this.mMusic.pause();
        } else {
            MainActivity.this.mMusic.play();
        }

        return scene;
    }

    @Override
    public void onLoadComplete() {

    }

    // Other methods


    private void checkFinish() {
        if (jhonLado == DERECHA && paulLado == DERECHA && georgeLado == DERECHA && ringoLado == DERECHA) {
            if (Integer.parseInt(puntaje.getText()) > 17) {

                mostrarDialogo("Tiempo excedido.");
                // mostrarToast("Game Over\nTiempo excedido.");
            } else {
                // mostrarToast("Felicidades!!!\nTiempo optimo.");
                mostrarDialogo("Felicidades!!!");
            }
        }
    }

    public void mostrarToast(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void mostrarDialogo(final String mensaje) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle(R.string.game_over);
                builder.setMessage(mensaje + " ¿Desea continuar?").setCancelable(false)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();

                                Intent intent = new Intent();
                                intent.setClass(MainActivity.this, MainActivity.this.getClass());

                                MainActivity.this.startActivity(intent);
                                MainActivity.this.finish();
                            }
                        }).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        MainActivity.this.finish();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
    }

    public boolean estanDosSeleccionados() {
        if (jhonSelected && paulSelected && !georgeSelected && !ringoSelected) {
            return true;
        }
        if (jhonSelected && !paulSelected && georgeSelected && !ringoSelected) {
            return true;
        }
        if (jhonSelected && !paulSelected && !georgeSelected && ringoSelected) {
            return true;
        }
        if (!jhonSelected && paulSelected && georgeSelected && !ringoSelected) {
            return true;
        }
        if (!jhonSelected && paulSelected && !georgeSelected && ringoSelected) {
            return true;
        }
        if (!jhonSelected && !paulSelected && georgeSelected && ringoSelected) {
            return true;
        }
        return false;
    }

    public boolean estaUnoSeleccionado() {
        if (jhonSelected && !paulSelected && !georgeSelected && !ringoSelected) {
            return true;
        }
        if (!jhonSelected && paulSelected && !georgeSelected && !ringoSelected) {
            return true;
        }
        if (!jhonSelected && !paulSelected && georgeSelected && !ringoSelected) {
            return true;
        }
        if (!jhonSelected && !paulSelected && !georgeSelected && ringoSelected) {
            return true;
        }
        return false;
    }

    public void moveJohnDerecha() {
        final Path ruta = new Path(4).to(inicialJohnX, inicialJohnY).to(200, inicialJohnY * 2).to(500, inicialJohnY * 2).to(finalJohnX, finalJohnY);
        sprites[JOHN].setCurrentTileIndex(1);
        move(ruta, sprites[JOHN], finalJohnX, finalJohnY);
        jhonLado = DERECHA;
    }

    public void movePaulDerecha1() {
        final Path ruta = new Path(2).to(inicialPaulX, inicialPaulY).to(finalPaulX, finalPaulY);
        move(ruta, sprites[PAUL], finalPaulX, finalPaulY);
        paulLado = DERECHA;
    }

    public void movePaulDerecha2() {
        final Path ruta = new Path(4).to(inicialPaulX, inicialPaulY).to(200, inicialPaulY + 70).to(500, inicialPaulY + 70).to(finalPaulX, finalPaulY);
        move(ruta, sprites[PAUL], finalPaulX, finalPaulY);
        paulLado = DERECHA;
    }

    public void moveGeorgeDerecha1() {
        final Path ruta = new Path(4).to(inicialGeorgeX, inicialGeorgeY).to(200, inicialGeorgeY - 70).to(500, inicialGeorgeY - 70)
                .to(finalGeorgeX, finalGeorgeY);
        move(ruta, sprites[GEORGE], finalGeorgeX, finalGeorgeY);
        georgeLado = DERECHA;
    }

    public void moveGeorgeDerecha2() {
        final Path ruta = new Path(2).to(inicialGeorgeX, inicialGeorgeY).to(finalGeorgeX, finalGeorgeY);
        move(ruta, sprites[GEORGE], finalGeorgeX, finalGeorgeY);
        georgeLado = DERECHA;
    }

    public void moveRingoDerecha() {
        final PathModifier.Path ruta = new Path(4).to(inicialRingoX, inicialRingoY).to(200, inicialRingoY - 70).to(500, inicialRingoY - 70)
                .to(finalRingoX, finalRingoY);
        move(ruta, sprites[RINGO], finalRingoX, finalRingoY);
        ringoLado = DERECHA;
    }

    public void moveJohnIzquierda() {
        final Path ruta = new Path(4).to(finalJohnX, finalJohnY).to(500, inicialJohnY + 120).to(200, inicialJohnY + 120)
                .to(inicialJohnX, inicialJohnY);
        move(ruta, sprites[JOHN], inicialJohnX, inicialJohnY);
        jhonLado = IZQUIERDA;
    }

    public void movePaulIzquierda() {
        final Path ruta = new Path(2).to(finalPaulX, finalPaulY).to(inicialPaulX, inicialPaulY);
        move(ruta, sprites[PAUL], inicialPaulX, inicialPaulY);
        paulLado = IZQUIERDA;
    }

    public void moveGeorgeIzquierda() {
        final Path ruta = new Path(2).to(finalGeorgeX, finalGeorgeY).to(inicialGeorgeX, inicialGeorgeY);
        move(ruta, sprites[GEORGE], inicialGeorgeX, inicialGeorgeY);
        georgeLado = IZQUIERDA;
    }

    public void moveRingoIzquierda() {
        final Path ruta = new Path(4)
                .to(finalRingoX, finalRingoY)
                .to(500, inicialRingoY - 120)
                .to(200, inicialRingoY - 120)
                .to(inicialRingoX, inicialRingoY);
        move(ruta, sprites[RINGO], inicialRingoX, inicialRingoY);
        ringoLado = IZQUIERDA;
    }

    private void move(Path ruta, AnimatedSprite animatedSprite, int finalX, int finalY) {
        animatedSprite.registerEntityModifier(new LoopEntityModifier(new PathModifier(VELOCIDAD, ruta, null, new CharacterRoute(animatedSprite, finalX,
                finalY), EaseSineInOut.getInstance()), 1));
    }
}
