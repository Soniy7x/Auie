package org.auie.ui;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.auie.utils.OnGifParseStatusListener;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

/**
 * 
 * UIGifView 项目托管地址http://code.google.com/p/gifview
 * 
 * @author ant.cy.liao
 */

@TargetApi(Build.VERSION_CODES.L)
public class UIGifView extends ImageView implements OnGifParseStatusListener {

	/** GIF解码器 */
	private UIGifDecoder gifDecoder = null;
	/** 当前要画的帧的图 */
	private Bitmap currentImage = null;
	private boolean isRun = true;
	private boolean pause = false;
	private DrawThread drawThread = null;
	private View backView = null;
	private GifImageType animationType = GifImageType.SYNC_DECODER;

	/**
	 * 解码过程中，GIF动画显示的方式
	 * 如果图片较大，那么解码过程会比较长，这个解码过程中，GIF如何显示
	 */
	public enum GifImageType {
		/**
		 * 在解码过程中，不显示图片，直到解码全部成功后，再显示
		 */
		WAIT_FINISH(0),
		/**
		 * 和解码过程同步，解码进行到哪里，图片显示到哪里
		 */
		SYNC_DECODER(1),
		/**
		 * 在解码过程中，只显示第一帧图片
		 */
		COVER(2);

		GifImageType(int i) {
			nativeInt = i;
		}

		final int nativeInt;
	}

	public UIGifView(Context context) {
		super(context);
		setScaleType(ImageView.ScaleType.FIT_XY);
	}

	public UIGifView(Context context, AttributeSet attrs) {
		super(context, attrs, 0);
	}

	public UIGifView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		setScaleType(ImageView.ScaleType.FIT_XY);
	}

	public UIGifView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		setScaleType(ImageView.ScaleType.FIT_XY);
	}

	/**
	 * 设置图片，并开始解码
	 * 
	 * @param gif
	 *            要设置的图片
	 */
	private void setGifDecoderImage(byte[] gif) {

		if (gifDecoder == null) {
			gifDecoder = new UIGifDecoder(this);
		}
		gifDecoder.setGifImage(gif);
		gifDecoder.start();
	}

	/**
	 * 设置图片，开始解码
	 * 
	 * @param is
	 *            要设置的图片
	 */
	private void setGifDecoderImage(InputStream is) {

		if (gifDecoder == null) {
			gifDecoder = new UIGifDecoder(this);
		}
		gifDecoder.setGifImage(is);
		gifDecoder.start();

	}

	/**
	 * 把本GIF动画设置为另外view的背景
	 * 
	 * @param v 要使用GIF作为背景的view
	 */
	public void setAsBackground(View v) {
		backView = v;
	}

	protected Parcelable onSaveInstanceState() {
		super.onSaveInstanceState();
		if (gifDecoder != null)
			gifDecoder.free();

		return null;
	}

	/**
	 * 以字节数据形式设置GIF图片
	 * 
	 * @param gif 图片
	 */
	public void setGifImage(byte[] gif) {
		setGifDecoderImage(gif);
	}

	/**
	 * 以字节流形式设置GIF图片
	 * 
	 * @param is 图片
	 */
	public void setGifImage(InputStream is) {
		setGifDecoderImage(is);
	}

	/**
	 * 以资源形式设置GIF图片
	 * 
	 * @param resId GIF图片的资源ID
	 */
	public void setGifImage(int resId) {
		Resources r = getResources();
		InputStream is = r.openRawResource(resId);
		setGifDecoderImage(is);
	}

	public void destroy() {
		if (gifDecoder != null)
			gifDecoder.free();
	}

	/**
	 * 只显示第一帧图片
	 * 调用本方法后，GIF不会显示动画，只会显示GIF的第一帧图
	 */
	public void showCover() {
		if (gifDecoder == null)
			return;
		pause = true;
		currentImage = gifDecoder.getImage();
		invalidate();
	}

	/**
	 * 继续显示动画
	 * 本方法在调用showCover后，会让动画继续显示，如果没有调用showCover方法，则没有任何效果
	 */
	public void showAnimation() {
		if (pause) {
			pause = false;
		}
	}

	/**
	 * 设置GIF在解码过程中的显示方式
	 * 本方法只能在setGifImage方法之前设置，否则设置无效
	 * 
	 * @param type 显示方式
	 */
	public void setGifImageType(GifImageType type) {
		if (gifDecoder == null)
			animationType = type;
	}

	@Override
	public void onParseCompleted(boolean parseStatus, int frameIndex) {
		if (parseStatus) {
			if (gifDecoder != null) {
				switch (animationType) {
				case WAIT_FINISH:
					if (frameIndex == -1) {
						if (gifDecoder.getFrameCount() > 1) { // 当帧数大于1时，启动动画线程
							DrawThread dt = new DrawThread();
							dt.start();
						} else {
							reDraw();
						}
					}
					break;
				case COVER:
					if (frameIndex == 1) {
						currentImage = gifDecoder.getImage();
						reDraw();
					} else if (frameIndex == -1) {
						if (gifDecoder.getFrameCount() > 1) {
							if (drawThread == null) {
								drawThread = new DrawThread();
								drawThread.start();
							}
						} else {
							reDraw();
						}
					}
					break;
				case SYNC_DECODER:
					if (frameIndex == 1) {
						currentImage = gifDecoder.getImage();
						reDraw();
					} else if (frameIndex == -1) {
						reDraw();
					} else {
						if (drawThread == null) {
							drawThread = new DrawThread();
							drawThread.start();
						}
					}
					break;
				}

			} else {
				Log.e("gif", "parse error");
			}

		}
	}

	private void reDraw() {
		if (redrawHandler != null) {
			Message msg = redrawHandler.obtainMessage();
			redrawHandler.sendMessage(msg);
		}

	}

	private void drawImage() {
		setImageBitmap(currentImage);
		invalidate();
	}

	@SuppressLint("HandlerLeak")
	private Handler redrawHandler = new Handler() {
		@SuppressWarnings("deprecation")
		public void handleMessage(Message msg) {
			try {
				if (backView != null) {
					backView.setBackgroundDrawable(new BitmapDrawable(
							currentImage));
				} else {
					drawImage();
				}
			} catch (Exception ex) {
				Log.e("GifView", ex.toString());
			}
		}
	};

	/**
	 * 动画线程
	 */
	private class DrawThread extends Thread {
		public void run() {
			if (gifDecoder == null) {
				return;
			}
			while (isRun) {
				if (gifDecoder.getFrameCount() == 1) {
					// 如果单帧，不进行动画
					UIGifFrame f = gifDecoder.next();
					currentImage = f.image;
					gifDecoder.free();
					reDraw();

					break;
				}
				if (pause == false) {
					UIGifFrame frame = gifDecoder.next();

					if (frame == null) {
						SystemClock.sleep(50);
						continue;
					}
					if (frame.image != null)
						currentImage = frame.image;
					else if (frame.imageName != null) {
						currentImage = BitmapFactory
								.decodeFile(frame.imageName);
					}
					long sp = frame.delay;
					if (redrawHandler != null) {
						reDraw();
						SystemClock.sleep(sp);
					} else {
						break;
					}
				} else {
					SystemClock.sleep(50);
				}
			}
		}
	}

	class UIGifFrame {
		/**
		 * 构造函数
		 * 
		 * @param im 图片
		 * @param del 延时
		 */
		public UIGifFrame(Bitmap im, int del) {
			image = im;
			delay = del;
		}

		public UIGifFrame(String name, int del) {
			imageName = name;
			delay = del;
		}

		/** 图片 */
		public Bitmap image;
		/** 延时 */
		public int delay;
		/** 当图片存成文件时的文件名 */
		public String imageName = null;

		/** 下一帧 */
		public UIGifFrame nextFrame = null;
	}

	class UIGifDecoder extends Thread {

		/** 状态：正在解码中 */
		public static final int STATUS_PARSING = 0;
		/** 状态：图片格式错误 */
		public static final int STATUS_FORMAT_ERROR = 1;
		/** 状态：打开失败 */
		public static final int STATUS_OPEN_ERROR = 2;
		/** 状态：解码成功 */
		public static final int STATUS_FINISH = -1;

		private InputStream in;
		private int status;

		public int width; // full image width
		public int height; // full image height
		private boolean gctFlag; // global color table used
		private int gctSize; // size of global color table
		private int loopCount = 1; // iterations; 0 = repeat forever

		private int[] gct; // global color table
		private int[] lct; // local color table
		private int[] act; // active color table

		private int bgIndex; // background color index
		private int bgColor; // background color
		private int lastBgColor; // previous bg color
		// private int pixelAspect; // pixel aspect ratio

		private boolean lctFlag; // local color table flag
		private boolean interlace; // interlace flag
		private int lctSize; // local color table size

		private int ix, iy, iw, ih; // current image rectangle
		private int lrx, lry, lrw, lrh;
		private Bitmap image; // current frame
		private Bitmap lastImage; // previous frame
		private UIGifFrame currentFrame = null;

		private boolean isShow = false;

		private byte[] block = new byte[256]; // current data block
		private int blockSize = 0; // block size

		// last graphic control extension info
		private int dispose = 0;
		// 0=no action; 1=leave in place; 2=restore to bg; 3=restore to prev
		private int lastDispose = 0;
		private boolean transparency = false; // use transparent color
		private int delay = 0; // delay in milliseconds
		private int transIndex; // transparent color index

		private static final int MaxStackSize = 4096;
		// max decoder pixel stack size

		// LZW decoder working arrays
		private short[] prefix;
		private byte[] suffix;
		private byte[] pixelStack;
		private byte[] pixels;

		private UIGifFrame gifFrame; // frames read from current file
		private int frameCount;

		private OnGifParseStatusListener listener = null;

		private byte[] gifData = null;
		/** 图片缓存的路径 */
		private String imagePath = null;
		/** 是否要缓存图片 */
		private boolean cacheImage = false;

		public UIGifDecoder(OnGifParseStatusListener action) {
			this.listener = action;
		}

		public void setGifImage(byte[] data) {
			gifData = data;
		}

		public void setGifImage(InputStream is) {
			in = is;
		}

		/**
		 * 设置是否要缓存图片
		 * 当设置要缓存图片时，解码出来的图片会直接写到文件中，而不保留在内存中，缓存时如果有SD卡，会直接缓存到SD卡中，
		 * 如果没有则会缓存到当前APK下的文件目录中
		 * 如果创建缓存目录失败，会自动切换为不缓存
		 * 默认不缓存
		 * 
		 * @param cache 是否要缓存，true要缓存
		 * @param context
		 */
		public void setCacheImage(boolean cache, Context context) {
			cacheImage = cache;
			try {
				if (cacheImage) {
					boolean f = true;

					if (Environment.getExternalStorageState().equals(
							Environment.MEDIA_MOUNTED)) {
						imagePath = android.os.Environment
								.getExternalStorageDirectory().getPath()
								+ File.separator
								+ "gifView_tmp_dir"
								+ File.separator + getDir();
						if (!createDir(imagePath)) {
							f = true;
						} else {
							f = false;
						}
					} else {
						f = true;
					}

					if (f) {
						imagePath = context.getFilesDir().getAbsolutePath()
								+ File.separator + getDir();
						if (!createDir(imagePath))
							cacheImage = false;
					}
				}
			} catch (Exception ex) {
				cacheImage = false;
			}
		}

		private void delDir(String folderPath, boolean dirDel) {
			try {
				delAllFile(folderPath);
				if (dirDel) {
					String filePath = folderPath;
					filePath = filePath.toString();
					java.io.File myFilePath = new java.io.File(filePath);
					myFilePath.delete();
				}
			} catch (Exception e) {

			}
		}

		private boolean delAllFile(String path) {
			boolean bea = false;
			File file = new File(path);
			if (!file.exists()) {
				return bea;
			}
			if (!file.isDirectory()) {
				return bea;
			}
			String[] tempList = file.list();
			File temp = null;
			for (int i = 0; i < tempList.length; i++) {
				if (path.endsWith(File.separator)) {
					temp = new File(path + tempList[i]);
				} else {
					temp = new File(path + File.separator + tempList[i]);
				}
				if (temp.isFile()) {
					temp.delete();
				} else if (temp.isDirectory()) {
					delAllFile(path + File.separator + tempList[i]);
					delDir(path + File.separator + tempList[i], true);
					bea = true;
				}
			}
			return bea;
		}

		private synchronized String getDir() {
			return String.valueOf(System.currentTimeMillis());
		}

		private boolean createDir(String path) {
			boolean ret = false;
			try {
				File file = new File(path);
				if (!file.exists()) {
					ret = file.mkdirs();

				} else
					ret = true;
			} catch (Exception e) {

				ret = false;
			}
			return ret;
		}

		private void saveImage(Bitmap image, String name) {
			try {
				new File(imagePath + File.separator + name + ".png");
				FileOutputStream fos = new FileOutputStream(imagePath
						+ File.separator + getDir() + ".png");
				image.compress(Bitmap.CompressFormat.PNG, 100, fos);
			} catch (Exception ex) {

			}
		}

		public void run() {
			if (in != null) {
				readStream();
			} else if (gifData != null) {
				readByte();
			}
		}

		/**
		 * 释放资源
		 */
		public void free() {
			UIGifFrame fg = gifFrame;
			if (cacheImage == false) {
				while (fg != null) {
					if (fg.image != null && !fg.image.isRecycled())
						fg.image.recycle();
					fg.image = null;
					fg = null;
					gifFrame = gifFrame.nextFrame;
					fg = gifFrame;
				}
			} else {
				delDir(imagePath, true);
			}
			if (in != null) {
				try {
					in.close();
				} catch (Exception ex) {
				}
				in = null;
			}
			gifData = null;
			status = 0;
			currentFrame = null;
		}

		/**
		 * 当前状态
		 * 
		 * @return
		 */
		public int getStatus() {
			return status;
		}

		/**
		 * 解码是否成功，成功返回true
		 * 
		 * @return 成功返回true，否则返回false
		 */
		public boolean parseOk() {
			return status == STATUS_FINISH;
		}

		/**
		 * 取某帧的延时时间
		 * 
		 * @param n 第几帧
		 * @return 延时时间，毫秒
		 */
		public int getDelay(int n) {
			delay = -1;
			if ((n >= 0) && (n < frameCount)) {
				// delay = ((GifFrame) frames.elementAt(n)).delay;
				UIGifFrame f = getFrame(n);
				if (f != null)
					delay = f.delay;
			}
			return delay;
		}

		/**
		 * 取所有帧的延时时间
		 * 
		 * @return
		 */
		public int[] getDelays() {
			UIGifFrame f = gifFrame;
			int[] d = new int[frameCount];
			int i = 0;
			while (f != null && i < frameCount) {
				d[i] = f.delay;
				f = f.nextFrame;
				i++;
			}
			return d;
		}

		/**
		 * 取总帧 数
		 * 
		 * @return 图片的总帧数
		 */
		public int getFrameCount() {
			return frameCount;
		}

		/**
		 * 取第一帧图片
		 * 
		 * @return
		 */
		public Bitmap getImage() {
			return getFrameImage(0);
		}

		public int getLoopCount() {
			return loopCount;
		}

		private void setPixels() {
			int[] dest = new int[width * height];
			// fill in starting image contents based on last image's dispose
			// code
			if (lastDispose > 0) {
				if (lastDispose == 3) {
					// use image before last
					int n = frameCount - 2;
					if (n > 0) {
						lastImage = getFrameImage(n - 1);
					} else {
						lastImage = null;
					}
				}
				if (lastImage != null) {
					lastImage.getPixels(dest, 0, width, 0, 0, width, height);
					// copy pixels
					if (lastDispose == 2) {
						// fill last image rect area with background color
						int c = 0;
						if (!transparency) {
							c = lastBgColor;
						}
						for (int i = 0; i < lrh; i++) {
							int n1 = (lry + i) * width + lrx;
							int n2 = n1 + lrw;
							for (int k = n1; k < n2; k++) {
								dest[k] = c;
							}
						}
					}
				}
			}

			// copy each source line to the appropriate place in the destination
			int pass = 1;
			int inc = 8;
			int iline = 0;
			for (int i = 0; i < ih; i++) {
				int line = i;
				if (interlace) {
					if (iline >= ih) {
						pass++;
						switch (pass) {
						case 2:
							iline = 4;
							break;
						case 3:
							iline = 2;
							inc = 4;
							break;
						case 4:
							iline = 1;
							inc = 2;
						}
					}
					line = iline;
					iline += inc;
				}
				line += iy;
				if (line < height) {
					int k = line * width;
					int dx = k + ix; // start of line in dest
					int dlim = dx + iw; // end of dest line
					if ((k + width) < dlim) {
						dlim = k + width; // past dest edge
					}
					int sx = i * iw; // start of line in source
					while (dx < dlim) {
						// map color and insert in destination
						int index = ((int) pixels[sx++]) & 0xff;
						int c = act[index];
						if (c != 0) {
							dest[dx] = c;
						}
						dx++;
					}
				}
			}
			image = Bitmap.createBitmap(dest, width, height, Config.ARGB_4444);
		}

		/**
		 * 取第几帧的图片
		 * 
		 * @param n 帧数
		 * @return 可画的图片，如果没有此帧或者出错，返回null
		 */
		public Bitmap getFrameImage(int n) {
			UIGifFrame frame = getFrame(n);
			if (frame == null)
				return null;
			else
				return frame.image;
		}

		/**
		 * 取当前帧图片
		 * 
		 * @hide
		 * @return 当前帧可画的图片
		 */
		public UIGifFrame getCurrentFrame() {
			return currentFrame;
		}

		/**
		 * 取第几帧，每帧包含了可画的图片和延时时间
		 * 
		 * @hide
		 * @param n 帧数
		 * @return
		 */
		public UIGifFrame getFrame(int n) {
			UIGifFrame frame = gifFrame;
			int i = 0;
			while (frame != null) {
				if (i == n) {
					return frame;
				} else {
					frame = frame.nextFrame;
				}
				i++;
			}
			return null;
		}

		/**
		 * 重置，进行本操作后，会直接到第一帧
		 * 
		 * @hide
		 */
		public void reset() {
			currentFrame = gifFrame;
		}

		/**
		 * 下一帧，进行本操作后，通过getCurrentFrame得到的是下一帧
		 * 
		 * @hide
		 * @return 返回下一帧
		 */
		public UIGifFrame next() {
			if (isShow == false) {
				isShow = true;
				return gifFrame;
			} else {
				if (currentFrame == null)
					return null;
				if (status == STATUS_PARSING) {
					if (currentFrame.nextFrame != null)
						currentFrame = currentFrame.nextFrame;
					// currentFrame = gifFrame;
				} else {
					currentFrame = currentFrame.nextFrame;
					if (currentFrame == null) {
						currentFrame = gifFrame;
					}
				}
				// if(cacheImage){
				// Log.d("===", "cache");
				// try{
				// currentFrame.image = BitmapFactory.decodeFile(imagePath +
				// File.separator + currentFrame.imageName);
				// }catch(Exception ex){
				// ex.printStackTrace();
				// }
				// }
				return currentFrame;
			}
		}

		private int readByte() {
			in = new ByteArrayInputStream(gifData);
			gifData = null;
			return readStream();
		}

		private int readStream() {
			init();
			if (in != null) {
				readHeader();
				if (!err()) {
					readContents();
					if (frameCount < 0) {
						status = STATUS_FORMAT_ERROR;
						listener.onParseCompleted(false, -1);
					} else {
						status = STATUS_FINISH;
						listener.onParseCompleted(true, -1);
					}
				}
				try {
					in.close();
				} catch (Exception e) {
					e.printStackTrace();
				}

			} else {
				status = STATUS_OPEN_ERROR;
				listener.onParseCompleted(false, -1);
			}
			return status;
		}

		private void decodeImageData() {
			int NullCode = -1;
			int npix = iw * ih;
			int available, clear, code_mask, code_size, end_of_information, in_code, old_code, bits, code, count, i, datum, data_size, first, top, bi, pi;

			if ((pixels == null) || (pixels.length < npix)) {
				pixels = new byte[npix]; // allocate new pixel array
			}
			if (prefix == null) {
				prefix = new short[MaxStackSize];
			}
			if (suffix == null) {
				suffix = new byte[MaxStackSize];
			}
			if (pixelStack == null) {
				pixelStack = new byte[MaxStackSize + 1];
			}
			// Initialize GIF data stream decoder.
			data_size = read();
			clear = 1 << data_size;
			end_of_information = clear + 1;
			available = clear + 2;
			old_code = NullCode;
			code_size = data_size + 1;
			code_mask = (1 << code_size) - 1;
			for (code = 0; code < clear; code++) {
				prefix[code] = 0;
				suffix[code] = (byte) code;
			}

			// Decode GIF pixel stream.
			datum = bits = count = first = top = pi = bi = 0;
			for (i = 0; i < npix;) {
				if (top == 0) {
					if (bits < code_size) {
						// Load bytes until there are enough bits for a code.
						if (count == 0) {
							// Read a new data block.
							count = readBlock();
							if (count <= 0) {
								break;
							}
							bi = 0;
						}
						datum += (((int) block[bi]) & 0xff) << bits;
						bits += 8;
						bi++;
						count--;
						continue;
					}
					// Get the next code.
					code = datum & code_mask;
					datum >>= code_size;
					bits -= code_size;

					// Interpret the code
					if ((code > available) || (code == end_of_information)) {
						break;
					}
					if (code == clear) {
						// Reset decoder.
						code_size = data_size + 1;
						code_mask = (1 << code_size) - 1;
						available = clear + 2;
						old_code = NullCode;
						continue;
					}
					if (old_code == NullCode) {
						pixelStack[top++] = suffix[code];
						old_code = code;
						first = code;
						continue;
					}
					in_code = code;
					if (code == available) {
						pixelStack[top++] = (byte) first;
						code = old_code;
					}
					while (code > clear) {
						pixelStack[top++] = suffix[code];
						code = prefix[code];
					}
					first = ((int) suffix[code]) & 0xff;
					// Add a new string to the string table,
					if (available >= MaxStackSize) {
						break;
					}
					pixelStack[top++] = (byte) first;
					prefix[available] = (short) old_code;
					suffix[available] = (byte) first;
					available++;
					if (((available & code_mask) == 0)
							&& (available < MaxStackSize)) {
						code_size++;
						code_mask += available;
					}
					old_code = in_code;
				}

				// Pop a pixel off the pixel stack.
				top--;
				pixels[pi++] = pixelStack[top];
				i++;
			}
			for (i = pi; i < npix; i++) {
				pixels[i] = 0; // clear missing pixels
			}
		}

		private boolean err() {
			return status != STATUS_PARSING;
		}

		private void init() {
			status = STATUS_PARSING;
			frameCount = 0;
			gifFrame = null;
			gct = null;
			lct = null;
		}

		private int read() {
			int curByte = 0;
			try {

				curByte = in.read();
			} catch (Exception e) {
				status = STATUS_FORMAT_ERROR;
			}
			return curByte;
		}

		private int readBlock() {
			blockSize = read();
			int n = 0;
			if (blockSize > 0) {
				try {
					int count = 0;
					while (n < blockSize) {
						count = in.read(block, n, blockSize - n);
						if (count == -1) {
							break;
						}
						n += count;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (n < blockSize) {
					status = STATUS_FORMAT_ERROR;
				}
			}
			return n;
		}

		private int[] readColorTable(int ncolors) {
			int nbytes = 3 * ncolors;
			int[] tab = null;
			byte[] c = new byte[nbytes];
			int n = 0;
			try {
				n = in.read(c);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (n < nbytes) {
				status = STATUS_FORMAT_ERROR;
			} else {
				tab = new int[256]; // max size to avoid bounds checks
				int i = 0;
				int j = 0;
				while (i < ncolors) {
					int r = ((int) c[j++]) & 0xff;
					int g = ((int) c[j++]) & 0xff;
					int b = ((int) c[j++]) & 0xff;
					tab[i++] = 0xff000000 | (r << 16) | (g << 8) | b;
				}
			}
			return tab;
		}

		private void readContents() {
			// read GIF file content blocks
			boolean done = false;
			while (!(done || err())) {
				int code = read();
				switch (code) {
				case 0x2C: // image separator
					readImage();
					break;
				case 0x21: // extension
					code = read();
					switch (code) {
					case 0xf9: // graphics control extension
						readGraphicControlExt();
						break;
					case 0xff: // application extension
						readBlock();
						String app = "";
						for (int i = 0; i < 11; i++) {
							app += (char) block[i];
						}
						if (app.equals("NETSCAPE2.0")) {
							readNetscapeExt();
						} else {
							skip(); // don't care
						}
						break;
					default: // uninteresting extension
						skip();
					}
					break;
				case 0x3b: // terminator
					done = true;
					break;
				case 0x00: // bad byte, but keep going and see what happens
					break;
				default:
					status = STATUS_FORMAT_ERROR;
				}
			}
		}

		private void readGraphicControlExt() {
			read(); // block size
			int packed = read(); // packed fields
			dispose = (packed & 0x1c) >> 2; // disposal method
			if (dispose == 0) {
				dispose = 1; // elect to keep old image if discretionary
			}
			transparency = (packed & 1) != 0;
			delay = readShort() * 10; // delay in milliseconds
			transIndex = read(); // transparent color index
			read(); // block terminator
		}

		private void readHeader() {
			String id = "";
			for (int i = 0; i < 6; i++) {
				id += (char) read();
			}
			if (!id.startsWith("GIF")) {
				status = STATUS_FORMAT_ERROR;
				return;
			}
			readLSD();
			if (gctFlag && !err()) {
				gct = readColorTable(gctSize);
				bgColor = gct[bgIndex];
			}
		}

		private void readImage() {
			ix = readShort(); // (sub)image position & size
			iy = readShort();
			iw = readShort();
			ih = readShort();
			int packed = read();
			lctFlag = (packed & 0x80) != 0; // 1 - local color table flag
			interlace = (packed & 0x40) != 0; // 2 - interlace flag
			// 3 - sort flag
			// 4-5 - reserved
			lctSize = 2 << (packed & 7); // 6-8 - local color table size
			if (lctFlag) {
				lct = readColorTable(lctSize); // read table
				act = lct; // make local table active
			} else {
				act = gct; // make global table active
				if (bgIndex == transIndex) {
					bgColor = 0;
				}
			}
			int save = 0;
			if (transparency) {
				save = act[transIndex];
				act[transIndex] = 0; // set transparent color if specified
			}
			if (act == null) {
				status = STATUS_FORMAT_ERROR; // no color table defined
			}
			if (err()) {
				return;
			}
			decodeImageData(); // decode pixel data
			skip();
			if (err()) {
				return;
			}
			frameCount++;
			// create new image to receive frame data
			image = Bitmap.createBitmap(width, height, Config.ARGB_4444);
			// createImage(width, height);
			setPixels(); // transfer pixel data to image
			if (gifFrame == null) {
				if (cacheImage) {
					String name = getDir();
					gifFrame = new UIGifFrame(imagePath + File.separator + name
							+ ".png", delay);
					saveImage(image, name);
				} else {
					gifFrame = new UIGifFrame(image, delay);
				}
				currentFrame = gifFrame;
			} else {
				UIGifFrame f = gifFrame;
				while (f.nextFrame != null) {
					f = f.nextFrame;
				}
				if (cacheImage) {
					String name = getDir();
					f.nextFrame = new UIGifFrame(imagePath + File.separator
							+ name + ".png", delay);
					saveImage(image, name);
				} else {
					f.nextFrame = new UIGifFrame(image, delay);
				}
			}
			// frames.addElement(new GifFrame(image, delay)); // add image to
			// frame
			// list
			if (transparency) {
				act[transIndex] = save;
			}
			resetFrame();
			listener.onParseCompleted(true, frameCount);
		}

		private void readLSD() {
			// logical screen size
			width = readShort();
			height = readShort();
			// packed fields
			int packed = read();
			gctFlag = (packed & 0x80) != 0; // 1 : global color table flag
			// 2-4 : color resolution
			// 5 : gct sort flag
			gctSize = 2 << (packed & 7); // 6-8 : gct size
			bgIndex = read(); // background color index
			// pixelAspect = read(); // pixel aspect ratio
		}

		private void readNetscapeExt() {
			do {
				readBlock();
				if (block[0] == 1) {
					// loop count sub-block
					int b1 = ((int) block[1]) & 0xff;
					int b2 = ((int) block[2]) & 0xff;
					loopCount = (b2 << 8) | b1;
				}
			} while ((blockSize > 0) && !err());
		}

		private int readShort() {
			// read 16-bit value, LSB first
			return read() | (read() << 8);
		}

		private void resetFrame() {
			lastDispose = dispose;
			lrx = ix;
			lry = iy;
			lrw = iw;
			lrh = ih;
			lastImage = image;
			lastBgColor = bgColor;
			dispose = 0;
			transparency = false;
			delay = 0;
			lct = null;
		}

		/**
		 * Skips variable length blocks up to and including next zero length
		 * block.
		 */
		private void skip() {
			do {
				readBlock();
			} while ((blockSize > 0) && !err());
		}
	}

}
