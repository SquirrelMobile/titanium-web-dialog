/**
 * This file was auto-generated by the Titanium Module SDK helper for Android
 * Appcelerator Titanium Mobile
 * Copyright (c) 2009-present by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Apache Public License
 * Please see the LICENSE included with this distribution for details.
 *
 */
package ti.webdialog;

import java.util.ArrayList;
import java.util.List;

import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollModule;
import org.appcelerator.kroll.annotations.Kroll;
import org.appcelerator.titanium.TiApplication;
import org.appcelerator.titanium.TiBlob;
import org.appcelerator.titanium.util.TiFileHelper;
import org.appcelerator.titanium.util.TiUIHelper;
import org.appcelerator.titanium.util.TiUrl;
import org.appcelerator.titanium.view.TiDrawableReference;
import org.appcelerator.kroll.common.Log;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.customtabs.CustomTabsIntent;
import android.support.customtabs.CustomTabsService;
import android.util.DisplayMetrics;


@Kroll.module(name="TitaniumWebDialog", id="ti.webdialog")
public class TitaniumWebDialogModule extends KrollModule
{
  // Standard Debugging variables
  private static final String LCAT = "TiWebDialog";
  
  private List<String> getCustomTabBrowsers(Context context, List<ResolveInfo> browsersList) {
    List<String> customTabBrowsers = new ArrayList<String>();
    
    for (ResolveInfo info : browsersList) {
      String packageName = info.activityInfo.packageName;
      
      Intent intent = new Intent();
      intent.setAction(CustomTabsService.ACTION_CUSTOM_TABS_CONNECTION);
      intent.setPackage(packageName);
      
      if (context.getPackageManager().resolveService(intent, 0) != null) {
        customTabBrowsers.add(packageName);
      }
    }
    
    return customTabBrowsers;
  }
  
  private void openCustomTab(Context context, List<String> customTabBrowsers, KrollDict options) {
    String URL = options.getString(Params.URL);
    CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
    builder.setShowTitle(Utils.getBool(options, Params.SHOW_TITLE));
    
    int barColor = Utils.getColor(options, Params.BAR_COLOR);
    if (barColor != -1) {
      builder.setToolbarColor(barColor);
    }
    
    // set start and exit animations
    if (Utils.getBool(options, Params.FADE_TRANSITION)) {
      builder.setStartAnimations(context, android.R.anim.fade_in, android.R.anim.fade_out);
      builder.setExitAnimations(context, android.R.anim.fade_in, android.R.anim.fade_out);
    }
    
    //enable Share link option
    if (Utils.getBool(options, Params.ENABLE_SHARING)) {
    	builder.addDefaultShareMenuItem();
    }
    
    String closeIcon = Utils.getString(options, Params.CLOSE_ICON); 
    if (!closeIcon.isEmpty()) {
    	builder.setCloseButtonIcon( getIcon(closeIcon) );
    }
    
    CustomTabsIntent tabIntent = builder.build();
    
    for(String s:customTabBrowsers) {
      tabIntent.intent.setPackage(s);
    }
    
    tabIntent.launchUrl(context, Uri.parse(URL));
  }

  
  private Bitmap getIcon(String path) {
	  Bitmap resultBitmap = null;
	  
	  if (path != null && !path.trim().equalsIgnoreCase("")) {
		  String resourceIcon = path.replaceAll(".png", "");
		  resourceIcon = "drawable." + resourceIcon;
		  
		  int resource = Utils.getR(resourceIcon);
		  
		  if (resource == 0) {
			  TiUrl imageUrl = new TiUrl(path);
			  TiFileHelper tfh = new TiFileHelper(TiApplication.getInstance());
			  Drawable d = tfh.loadDrawable(imageUrl.resolve(), false);
			  
			  resultBitmap = ((BitmapDrawable) d).getBitmap();
				
		  } else {
			  resultBitmap = BitmapFactory.decodeResource(TiApplication.getAppRootOrCurrentActivity().getResources(), resource);
		  }
	  }
	  
	  // important step to show close icon
	  // rescale bitmap to 24dp(Height) x 48dp(Width) as mentioned here, otherwise it won't work
	  // https://developer.chrome.com/multidevice/android/customtabs#choosing-an icon for the action button
	  if (resultBitmap != null) {
		  resultBitmap = Utils.rescaleBitmap(TiApplication.getAppRootOrCurrentActivity(), resultBitmap, 24, 48);
	  }
	  
	  return resultBitmap;
  }

  
  @Kroll.method
  public void open(KrollDict options) {
    if ((options != null) && options.containsKeyAndNotNull(Params.URL)) {
      Context context = TiApplication.getAppCurrentActivity();
      List<ResolveInfo> browsersList = Utils.allBrowsers(context);
      
      if (!browsersList.isEmpty()) {
        List<String> customTabBrowsers = getCustomTabBrowsers(context, browsersList);
        
        // show supported browsers list or open directly if only 1 supported browser is available
        openCustomTab(context, customTabBrowsers, options);
      } else {
        Log.i(Params.LCAT, "No browsers available in this device.");
      }
    }
  }

  @Kroll.method
  public boolean isSupported() {
    Context context = TiApplication.getAppCurrentActivity();
    List<ResolveInfo> browsersList = Utils.allBrowsers(context);
    
    return !browsersList.isEmpty();
  }
  
  @Kroll.method
  public void close(KrollDict options) {
    Log.e(Params.LCAT, "The \"close\" method is not implemented, yet!");
  }
  
  @Kroll.method
  public boolean isOpen(KrollDict options) {
    Log.e(Params.LCAT, "The \"isOpen\" method is not implemented, yet!");
    return false;
  }
}
