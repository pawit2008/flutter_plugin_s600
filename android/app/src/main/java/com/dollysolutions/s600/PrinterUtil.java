package com.dls.thai_id_card_reader_plugin;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import com.example.thai_id_card_reader_app.R;

import com.kp.ktsdkservice.printer.AidlPrinterListener;
import com.kp.ktsdkservice.printer.PrintItemObj;
import com.kp.ktsdkservice.service.AidlDeviceService;
import com.kp.ktsdkservice.printer.AidlPrinter;

import java.io.ByteArrayOutputStream;
import android.os.RemoteException;

public class PrinterUtil {

    public static void printReceipt(Context context, AidlPrinter aidlPrinter) {
        Log.d("PrinterUtil", "🖨️ printReceipt called");

        try {

            aidlPrinter.printText(new ArrayList<PrintItemObj>() {
                {
                    add(new PrintItemObj("(English) what are you doing now", 24, true));

                    add(new PrintItemObj("默认打印数据测试"));
                    add(new PrintItemObj("打印数据字体放大", 24));
                    add(new PrintItemObj("打印数据加粗", 8, true));
                    add(new PrintItemObj("打印数据左对齐测试", 8, false, PrintItemObj.ALIGN.LEFT));
                    add(new PrintItemObj("打印数据居中对齐测试", 8, false, PrintItemObj.ALIGN.CENTER));
                    add(new PrintItemObj("打印数据右对齐测试", 8, false, PrintItemObj.ALIGN.RIGHT));
                    add(new PrintItemObj("打印数据下划线", 8, false, PrintItemObj.ALIGN.LEFT, true));

                    add(new PrintItemObj("打印数据不换行测试打印数据不换行测试打印数据不换行测试", 8, false, PrintItemObj.ALIGN.LEFT, false,
                            false));
                    add(new PrintItemObj("打印数据不换行测试", 8, false, PrintItemObj.ALIGN.LEFT, false, false));
                    add(new PrintItemObj("打印数据行间距测试", 8, false, PrintItemObj.ALIGN.LEFT, false, true, 40));
                    add(new PrintItemObj("打印数据行间距测试", 8, false, PrintItemObj.ALIGN.LEFT, false, true, 83));
                    add(new PrintItemObj("打印数据行间距测试", 8, false, PrintItemObj.ALIGN.LEFT, false, true, 40));
                    add(new PrintItemObj("打印数据字符间距测试", 8, false, PrintItemObj.ALIGN.LEFT, false, true, 29, 25));
                    add(new PrintItemObj("打印数据字符间距测试", 8, false, PrintItemObj.ALIGN.LEFT, false, true, 29, 25));
                    add(new PrintItemObj("打印数据字符间距测试\r\n\r\n\r\n", 8, false, PrintItemObj.ALIGN.LEFT, false, true, 29,
                            25));

                }
            }, new AidlPrinterListener.Stub() {
                @Override
                public void onPrintFinish() throws RemoteException {
                    aidlPrinter.prnStart();
                    aidlPrinter.printClose();
                    // String endTime = getCurTime();
                    // showMessage(getResources().getString(R.string.print_end_time) + endTime);
                }

                @Override
                public void onError(int arg0) throws RemoteException {
                    // showMessage(getResources().getString(R.string.print_error_code) + arg0);
                }
            });

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View receiptView = inflater.inflate(R.layout.receipt_layout, null);

            Log.d("PrinterUtil", "✅ Layout inflated");

            receiptView.setDrawingCacheEnabled(true);
            receiptView.measure(View.MeasureSpec.makeMeasureSpec(384, View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.UNSPECIFIED);
            receiptView.layout(0, 0, receiptView.getMeasuredWidth(), receiptView.getMeasuredHeight());

            Bitmap receiptBitmap = Bitmap.createBitmap(receiptView.getDrawingCache());
            receiptView.setDrawingCacheEnabled(false);

            if (aidlPrinter == null) {
                Log.e("ThaiPlugin", "❌ printer is NULL");
                return;
            }

            Log.d("PrinterUtil", "✅ Bitmap created, sending to printer");

            aidlPrinter.printBmp(0, receiptBitmap.getWidth(), receiptBitmap.getHeight(), receiptBitmap,
                    new AidlPrinterListener.Stub() {
                        @Override
                        public void onPrintFinish() throws RemoteException {
                            Log.d("PrinterUtil", "✅ Print finished");
                            aidlPrinter.prnStart();
                            aidlPrinter.printClose();
                        }

                        @Override
                        public void onError(int code) throws RemoteException {
                            Log.e("PrinterUtil", "❌ Print error: " + code);
                        }
                    });

        } catch (Exception e) {
            Log.e("PrinterUtil", "❌ Exception in printReceipt", e);
        }
    }

}
