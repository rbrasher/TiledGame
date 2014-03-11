package Util;

import org.andengine.audio.music.Music;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.util.debug.Debug;
import org.andengine.util.texturepack.TexturePack;
import org.andengine.util.texturepack.TexturePackLoader;
import org.andengine.util.texturepack.TexturePackTextureRegionLibrary;
import org.andengine.util.texturepack.exception.TexturePackParseException;

import SpriteSheets.CharactersTexturePacker;
import android.graphics.Color;

import com.ronb.tiledgame.WorldActivity;

/**
 * The main activity will load all the textures from here
 * 
 * @author ron
 *
 */
public class AssetLoaderUtil {

	// ==========================================
	// TEXTURES
	// ==========================================
	public static BitmapTextureAtlas mFontMENUTexture;
	public static BitmapTextureAtlas mFontINFOTexture;
	public static BitmapTextureAtlas mFontTEXTTexture;
	public static Font mFontMENU;
	public static Font mFontTEXT;
	public static Font mFontINFO;
	
	public static TiledTextureRegion mPlayerTextureRegion;
	
	public static TexturePackerTextureRegion mDPADTextureRegion;
	public static TexturePackerTextureRegion mDPADBackingTextureRegion;
	public static TexturePackerTextureRegion FadeTextureRegion;
	public static TexturePackerTextureRegion mLogoTextureRegion;
	public static TexturePackerTextureRegion mMainMenuTextureRegion;
	
	public static TexturePackTextureRegionLibrary mCharactersTexturePackTextureRegionLibrary;
	public static TexturePackTextureRegionLibrary mWorldTexturePackTextureRegionLibrary;
	public static TexturePackTextureRegionLibrary mUtilTexturePackTextureRegionLibrary;
	
	//Music and Sounds
	public static Music WorldTheme;
	
	public static void loadAllSound() {
		//loads the sounds
		
	}
	
	public static void loadAllMusic() {
		//loads all the music
		
	}
	
	public static void loadAllFonts(WorldActivity pMain) {
		//load font textures
		mFontMENUTexture = new BitmapTextureAtlas(pMain.getTextureManager(), 256, 256, TextureOptions.BILINEAR);
		mFontINFOTexture = new BitmapTextureAtlas(pMain.getTextureManager(), 256, 256, TextureOptions.BILINEAR);
		mFontTEXTTexture = new BitmapTextureAtlas(pMain.getTextureManager(), 256, 256, TextureOptions.BILINEAR);
		FontFactory.setAssetBasePath("font/");
		mFontMENU = FontFactory.createFromAsset(pMain.getFontManager(), mFontMENUTexture, pMain.getAssets(), "Zrnic.ttf", 24, true, Color.WHITE);
		mFontMENU.load();
		mFontTEXT = FontFactory.createFromAsset(pMain.getFontManager(), mFontTEXTTexture, pMain.getAssets(), "Zrnic.ttf", 26, true, Color.WHITE);
		mFontTEXT.load();
		mFontINFO = FontFactory.createFromAsset(pMain.getFontManager(), mFontINFOTexture, pMain.getAssets(), "Zrnic.ttf", 18, true, Color.WHITE);
		mFontINFO.load();
	}
	
	/**
	 * Using texture packer for all the textures
	 * 
	 * @param pMain
	 */
	public static void loadAllTextures(WorldActivity pMain) {
		
		try {
			//Util texture packer
			TexturePack spriteSheetUtilTexturePack = new TexturePackLoader(pMain.getTextureManager(), "SpriteSheets/Util/").loadFromAsset(pMain.getAssets(), "Util.xml");
			spriteSheetUtilTexturePack.loadTexture();
			mUtilTexturePackTextureRegionLibrary = spriteSheetUtilTexturePack.getTexturePackTextureRegionLibrary();
			
			//characters texture packer
			TexturePack spriteSheetCharactersTexturePack = new TexturePackLoader(pMain.getTextureManager(), "SpriteSheets/Characters/").loadFromAsset(pMain.getAssets(), "CharactersTexturePacker.xml");
			spriteSheetCharactersTexturePack.loadTexture();
			mCharactersTexturePackTextureRegionLibrary = spriteSheetCharactersTexturePack.getTexturePackTextureRegionLibrary();
		} catch (final TexturePackParseException e) {
			Debug.e(e);
		}
		
		//Util textures
		FadeTextureRegion = mUtilTexturePackTextureRegionLibrary.get(Util.FADEBACKGROUND_ID);
		mLogoTextureRegion = mUtilTexturePackTextureRegionLibrary.get(Util.SPLASHSCREEN_ID);
		mMainMenuTextureRegion = mUtilTexturePackTextureRegionLibrary.get(Util.MAINMENU_ID);
		
		//creating tiled region
		TexturePackerTextureRegion HeroTextureRegion = mCharactersTexturePackTextureRegionLibrary.get(CharactersTexturePacker.HERO_ID);
		mPlayerTextureRegion = TiledTextureRegion.create(HeroTextureRegion.getTexture(), (int)HeroTextureRegion.getTextureX(), (int)HeroTextureRegion.getTextureY(), (int)HeroTextureRegion.getWidth(), (int)HeroTextureRegion.getHeight(), 3, 4);
		
		//DPAD
		mDPADTextureRegion = mUtilTexturePackTextureRegionLibrary.get(Util.ONSCREEN_CONTROL_KNOB_ID);
		mDPADBackingTextureRegion = mUtilTexturePackTextureRegionLibrary.get(Util.ONSCREEN_CONTROL_BASE_ID);
	}
}
