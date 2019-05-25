package com.martiply.model;

import android.content.Context;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.request.RequestOptions;
import com.martiply.model.interfaces.AbsImg;
import org.parceler.Parcel;
import org.parceler.ParcelConstructor;

import java.util.List;

@Parcel
public class Img extends AbsImg {

	final List<String> paths;
	final String imgHost;
	final AbsImg.Root root;

	@ParcelConstructor
	public Img(List<String> paths, String imgHost, AbsImg.Root root) {
		this.paths = paths;
		this.imgHost = imgHost;
		this.root = root;
	}

	@Override
	public List<String> getPaths() {
		return paths;
	}

	@Override
	public String getImgHost() { return imgHost; }

	@Override
	public AbsImg.Root getRoot() { return root; }

	public static void loadImg(Img img, int which, Context ctx, ImageView view, int placeHolderRes, int errorRes, AbsImg.Size size){
		if(img == null){
			Glide.with(ctx)
					.load(errorRes)
					.into(view);
		}else{
			RequestOptions options = new RequestOptions()
					.centerCrop()
					.placeholder(placeHolderRes)
					.error(errorRes)
					.priority(Priority.HIGH);

			String url = AbsImg.urlOf(img, which, size);
			Glide.with(ctx)
					.load(url)
					.apply(options)
					.into(view);
		}
	}
}
