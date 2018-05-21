package com.passaparola.thiagodesales.passaparolaview.files;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.net.Uri;
import android.text.TextPaint;
import android.util.Log;
import android.widget.ImageView;

import com.passaparola.thiagodesales.passaparolaview.R;
import com.passaparola.thiagodesales.passaparolaview.model.RSSMeditationItem;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;

public class FileManager {

    private static FileManager instance;
    private Context context;

    private FileManager(Context context) {
        this.context = context;
    }

    public synchronized static FileManager getInstance(Context context) {
        if (instance == null)
            instance = new FileManager(context);

        return instance;
    }

    public void drawPictureForFileSharing(RSSMeditationItem meditationItem) {
        Resources resources = context.getResources();
        float scale = resources.getDisplayMetrics().density;

        Log.d("PictureForFileSharing", "scale: " + scale );

        Bitmap bitmap = BitmapFactory.decodeResource(resources, R.drawable.background4parola);

        android.graphics.Bitmap.Config bitmapConfig = bitmap.getConfig();
        bitmap = bitmap.copy(bitmapConfig, true);

        Canvas canvas = new Canvas(bitmap);
        Rect bounds = new Rect();

        TextPaint textPaint = new TextPaint();
        textPaint.setTextSize(180);
        textPaint.setTextAlign(Paint.Align.LEFT);
        textPaint.setColor(Color.rgb(219,139,255));
        textPaint.setTypeface(Typeface.create("sans-serif-medium", Typeface.BOLD));

        Calendar c = Calendar.getInstance();
        String day = new Integer(c.get(Calendar.DATE)).toString();
        textPaint.getTextBounds(day, 0, day.length(), bounds);
        float xConstant = (float) 34.2857142857;
        float yConstant = (float) 228.5714285714;
        canvas.drawText(day, scale * xConstant, scale * yConstant, textPaint);

        textPaint.setTextSize(100);
        textPaint.setTypeface(Typeface.create("Arial", Typeface.NORMAL));

        String month = resources.getStringArray(R.array.months)[c.get(Calendar.MONTH)];
        textPaint.getTextBounds(month, 0, month.length(), bounds);
        xConstant = (float) 118.0952380952;
        yConstant = (float) 205.7142857143;
        canvas.drawText(month, scale * xConstant, scale * yConstant, textPaint);

        String year = new Integer(c.get(Calendar.YEAR)).toString();
        textPaint.getTextBounds(year, 0, year.length(), bounds);
        xConstant = (float) 118.0952380952;
        yConstant = (float) 236.1904761905;
        canvas.drawText(year, scale * xConstant, scale * yConstant, textPaint);
        //-----------------------------------------------------

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.rgb(255,255, 225));
        paint.setTextSize((int) (55 * scale));
        paint.setShadowLayer(1f, 0f, 1f, Color.DKGRAY);

        String parola = meditationItem.getParola(meditationItem.getCurrentParolaLanguage());
        paint.getTextBounds(parola, 0, parola.length(), bounds);

        float constantLineSpace = (float) (scale * 49.5238095238);

        if (parola.length() <= 23) {
            xConstant = (float) 228.5714285714;
            yConstant = (float) 76.1904761905;
            canvas.drawText(parola, scale * xConstant, scale * yConstant, paint);
        } else {
            String partes[] = parola.split(" ");
            int soma = 0;
            xConstant = (float) 228.5714285714;
            yConstant = (float) 53.3333333333;
            float y = scale * yConstant;
            int end = 23;
            String lines = "";

            for (int i = 0; i < partes.length; i++) {
                soma = soma + partes[i].length() + 1;

                if (soma <= end) {
                    lines = lines + partes[i] + " ";
                } else {
                    canvas.drawText(lines, scale * xConstant, y, paint);
                    y = y + constantLineSpace;
                    lines = partes[i] + " ";
                    soma = lines.length();
                }
            }

            canvas.drawText(lines, scale * xConstant, y, paint);
        }

        //---------------------------------------------
        paint.setColor(Color.rgb(0,0, 225));
        paint.setTextSize((int) (35 * scale));

        String meditation = meditationItem.getMeditation(meditationItem.getCurrentParolaLanguage());
        paint.getTextBounds(meditation, 0, meditation.length(), bounds);

        String partes[] = meditation.split(" ");
        String lines = "";
        int soma = 0;
        int end = 43;
        yConstant = (float) 209.5238095238;
        float y = scale * yConstant;
        for(int i = 0; i < partes.length; i++) {
            soma = soma + partes[i].length() + 1;
            if (soma <= end)
                lines = lines + partes[i] + " ";
            else {
                canvas.drawText(lines, scale * xConstant, y, paint);
                y = y + constantLineSpace;
                lines = partes[i] + " ";
                soma = lines.length();
            }
        }
        canvas.drawText(lines, scale * xConstant, y, paint);

//        ImageView imageView = (ImageView) context.findViewById(R.id.imageView6);
//        imageView.setImageBitmap(bitmap);

        try {
            ContextWrapper wrapper = new ContextWrapper(context);
            File file = wrapper.getDir("Images", context.MODE_PRIVATE);
            file = new File(file, "parola17"+".png");

            OutputStream outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            outputStream.flush();
            outputStream.close();

            Uri savedImageURI = Uri.parse(file.getAbsolutePath());

            Log.d("draw", "Image saved in internal storage.\n" + savedImageURI);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
