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
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.text.TextPaint;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.passaparola.thiagodesales.passaparolaview.BuildConfig;
import com.passaparola.thiagodesales.passaparolaview.R;
import com.passaparola.thiagodesales.passaparolaview.model.RSSMeditationItem;
import com.passaparola.thiagodesales.passaparolaview.utils.Utils;

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

    public Uri drawPictureForFileSharing(RSSMeditationItem meditationItem) {
        Log.d("drawPictureForFile", "Criando imagem para parola " + meditationItem.getParolas().get("pt"));
        String parola = meditationItem.getParola(meditationItem.getCurrentParolaLanguage());
        String meditation = meditationItem.getMeditation(meditationItem.getCurrentParolaLanguage());
        Log.d("drawPict...", "Numero de caracts: " + parola.length() + " - " + meditation.length());

        if (parola.length() + meditation.length() <= 854) {
            Resources resources = context.getResources();
            float scale = resources.getDisplayMetrics().density;

            String backgroundName  = Utils.sortBackgroundForSharing();
            Log.d("drawPict...", "Background sorteado: " + backgroundName);
            int backgroundId = context.getResources().getIdentifier( backgroundName, "drawable", context.getPackageName());

            Bitmap bitmap = BitmapFactory.decodeResource(resources, backgroundId);

            android.graphics.Bitmap.Config bitmapConfig = bitmap.getConfig();
            bitmap = bitmap.copy(bitmapConfig, true);

            Canvas canvas = new Canvas(bitmap);
            Rect bounds = new Rect();

            TextPaint textPaint = new TextPaint();
            textPaint.setTextSize(180);
            textPaint.setTextAlign(Paint.Align.LEFT);
            textPaint.setColor(Color.rgb(0,0,225));
            textPaint.setTypeface(Typeface.create("sans-serif-medium", Typeface.BOLD));

            String meditationPublishedDateParts[] = Utils.isoDateToStandardFormat(meditationItem.getPublishedDate()).split("/");
            String day = meditationPublishedDateParts[0];
            textPaint.getTextBounds(day, 0, day.length(), bounds);
            float xConstant = (float) 77.2857142857;
            float yConstant = (float) 83.5714285714;
            canvas.drawText(day, scale * xConstant, scale * yConstant, textPaint);

            textPaint.setTextSize(100);
            textPaint.setTypeface(Typeface.create("Arial", Typeface.NORMAL));

            String month = resources.getStringArray(R.array.months)[Integer.valueOf(meditationPublishedDateParts[1])-1];
            textPaint.getTextBounds(month, 0, month.length(), bounds);
            xConstant = (float) 80.0952380952;
            yConstant = (float) 120.7142857143;
            canvas.drawText(month, scale * xConstant, scale * yConstant, textPaint);

            String year = meditationPublishedDateParts[2];
            textPaint.getTextBounds(year, 0, year.length(), bounds);
            xConstant = (float) 80.0952380952;
            yConstant = (float) 157.1904761905;
            canvas.drawText(year, scale * xConstant, scale * yConstant, textPaint);
            //-----------------------------------------------------

            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setColor(Color.rgb(0,0, 225));
            paint.setTextSize((int) (55 * scale));
            paint.setShadowLayer(1f, 0f, 1f, Color.DKGRAY);


            paint.getTextBounds(parola, 0, parola.length(), bounds);

            float constantLineSpace = (float) (scale * 49.5238095238);
            float parolaY = 0;
            xConstant = (float) 228.5714285714;
            if (parola.length() <= 23) {
                yConstant = (float) 76.1904761905;
                canvas.drawText(parola, scale * xConstant, scale * yConstant, paint);
            } else {
                String partes[] = parola.split(" ");
                int soma = 0;
                yConstant = (float) 53.3333333333;
                parolaY = scale * yConstant;
                int end = 23;
                String lines = "";

                for (int i = 0; i < partes.length; i++) {
                    soma = soma + partes[i].length() + 1;

                    if (soma <= end) {
                        lines = lines + partes[i] + " ";
                    } else {
                        canvas.drawText(lines, scale * xConstant, parolaY, paint);
                        parolaY = parolaY + constantLineSpace;
                        lines = partes[i] + " ";
                        soma = lines.length();
                    }
                }

                canvas.drawText(lines, scale * xConstant, parolaY, paint);
            }

            //---------------------------------------------
            paint.setColor(Color.rgb(0,0, 225));
            paint.setTextSize((int) (35 * scale));

            paint.getTextBounds(meditation, 0, meditation.length(), bounds);

            String partes[] = meditation.split(" ");
            String lines = "";
            int soma = 0;
            int end = 43;
//        yConstant = parolaY;
            float y = parolaY + constantLineSpace + 180;
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
            Log.d("drawPictureFor...", "Total Y = " + y);
            canvas.drawText(lines, scale * xConstant, y, paint);

//        ImageView imageView = (ImageView) context.findViewById(R.id.imageView6);
//        imageView.setImageBitmap(bitmap);

            Uri bmpUri = null;
            File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "share_image_" + System.currentTimeMillis() + ".png");
            FileOutputStream out = null;
            try {
                out = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
                out.close();

                bmpUri = FileProvider.getUriForFile(context, "com.passaparola.thiagodesales.passaparolaview", file);
            } catch (FileNotFoundException e) {
                Toast.makeText(context, "Erro ao gerar imagem. Tente compartilhar apenas texto!", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                Toast.makeText(context, "Erro ao gerar imagem. Tente compartilhar apenas texto!", Toast.LENGTH_SHORT).show();
            }

            return bmpUri;
        } else
            return null;

    }

}
