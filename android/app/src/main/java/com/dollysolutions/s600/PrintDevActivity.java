package com.dollysolutions.s600;

import static com.dollysolutions.s600.util.getCurTime;
import static com.kp.ktsdkservice.data.PrinterConstant.BarCodeType.CODE_39;
import com.example.thai_id_card_reader_app.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.kp.ktsdkservice.printer.AidlPrinterListener;
import com.kp.ktsdkservice.printer.PrintItemObj;
import com.kp.ktsdkservice.service.AidlDeviceService;
import com.kp.ktsdkservice.printer.AidlPrinter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import android.app.Activity;

public class PrintDevActivity extends Activity {
	private AidlPrinter aidlPrinter = SmartPosApplication.getInstance().printer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	public void printQrCode(View v) {
		Bitmap qrcodeBitmap = QRCodeUtil.createQRImage("123456789", 300, 300, null);
		try {
			aidlPrinter.printBmp(0, qrcodeBitmap.getWidth(), qrcodeBitmap.getHeight(), qrcodeBitmap,
					new AidlPrinterListener.Stub() {
						@Override
						public void onPrintFinish() throws RemoteException {
							aidlPrinter.prnStart();
							aidlPrinter.printClose();
							// showMessage(getResources().getString(R.string.print_qrcode_success));
						}

						@Override
						public void onError(int arg0) throws RemoteException {
							// showMessage(getResources().getString(R.string.print_qrcode_error) + arg0);
						}
					});
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void getPrintState(View v) {
		try {
			int printState = aidlPrinter.getPrinterState();
			// showMessage(getResources().getString(R.string.print_status) + printState);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void printReceipt() {
		try {
			// 1. Inflate the custom layout
			LayoutInflater inflater = null;
			if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
				inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			}
			View receiptView = inflater.inflate(R.layout.receipt_layout, null);

			// TextView serviceItemDetail =
			// receiptView.findViewById(R.id.service_item_detail);
			// serviceItemDetail.setText("头部清洁 x2");

			// TextView amountValue = receiptView.findViewById(R.id.amount_value);
			// amountValue.setText("40.00");

			// 3. Measure and layout the view
			receiptView.setDrawingCacheEnabled(true);
			receiptView.measure(View.MeasureSpec.makeMeasureSpec(384, View.MeasureSpec.EXACTLY),
					View.MeasureSpec.UNSPECIFIED);
			receiptView.layout(0, 0, receiptView.getMeasuredWidth(), receiptView.getMeasuredHeight());

			// 4. Create a bitmap from the view
			Bitmap receiptBitmap = Bitmap.createBitmap(receiptView.getDrawingCache());
			receiptView.setDrawingCacheEnabled(false);

			// 5. Print the bitmap using your existing AidlPrinter
			aidlPrinter.printBmp(0, receiptBitmap.getWidth(), receiptBitmap.getHeight(), receiptBitmap,
					new AidlPrinterListener.Stub() {
						@Override
						public void onPrintFinish() throws RemoteException {
							aidlPrinter.prnStart();
							aidlPrinter.printClose();
							// showMessage(getResources().getString(R.string.print_success));
						}

						@Override
						public void onError(int arg0) throws RemoteException {
							// showMessage(getResources().getString(R.string.print_error_code) + arg0);
						}
					});
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	public void printText(View v) {
		try {
			String startTime = getCurTime();
			// showMessage(getResources().getString(R.string.print_start_time) + startTime);
			aidlPrinter.printText(new ArrayList<PrintItemObj>() {
				{
					add(new PrintItemObj("مرحبا بكم في(Arabic)", 24, true));
					add(new PrintItemObj("(English) what are you doing now", 24, true));
					add(new PrintItemObj("0123456789123456789", 24, true));
					add(new PrintItemObj("1234567890123456789", 24, true));
					add(new PrintItemObj("բարի գալուստ:", 24, true));
					// add(new PrintItemObj("LACoil Բարի գալուստ\n" +
					// "=========================\n" +
					// " ԱՆԴՈՐԱԳԻՐԸ N129257\n" +
					// "Ամսաթիվ: 04.03.2024 12:11:52\n" +
					// " Տերմինալ։ 054\n" +
					// " Լց. հասցե։ ք. Վանաձոր, Երևանյա\n" +
					// " ն խճ. 140\n" +
					// " ՎԱՃԱՌՔ\n" +
					// " Դիզել\n" +
					// " 30 լիտր x 540 = 16200 դրամ\n" +
					// " ՀԱՍՏԱՏՎԵԼ Է\n" +
					// " Քարտ 31261292\n" +
					// "=========================\n" +
					// " Ոչ ֆիսկալ փաստաթուղթ\n" +
					// " Շնորհակալություն։ Բարի Երթ!\n",));
					add(new PrintItemObj("默认打印数据测试"));
					add(new PrintItemObj("默认打印数据测试"));
					add(new PrintItemObj("打印数据字体放大", 24));
					add(new PrintItemObj("打印数据字体放大", 24));
					add(new PrintItemObj("打印数据字体放大", 24));
					add(new PrintItemObj("打印数据加粗", 8, true));
					add(new PrintItemObj("打印数据加粗", 8, true));
					add(new PrintItemObj("打印数据加粗", 8, true));
					add(new PrintItemObj("打印数据左对齐测试", 8, false, PrintItemObj.ALIGN.LEFT));
					add(new PrintItemObj("打印数据左对齐测试", 8, false, PrintItemObj.ALIGN.LEFT));
					add(new PrintItemObj("打印数据左对齐测试", 8, false, PrintItemObj.ALIGN.LEFT));
					add(new PrintItemObj("打印数据居中对齐测试", 8, false, PrintItemObj.ALIGN.CENTER));
					add(new PrintItemObj("打印数据居中对齐测试", 8, false, PrintItemObj.ALIGN.CENTER));
					add(new PrintItemObj("打印数据居中对齐测试", 8, false, PrintItemObj.ALIGN.CENTER));
					add(new PrintItemObj("打印数据右对齐测试", 8, false, PrintItemObj.ALIGN.RIGHT));
					add(new PrintItemObj("打印数据右对齐测试", 8, false, PrintItemObj.ALIGN.RIGHT));
					add(new PrintItemObj("打印数据右对齐测试", 8, false, PrintItemObj.ALIGN.RIGHT));
					add(new PrintItemObj("打印数据下划线", 8, false, PrintItemObj.ALIGN.LEFT, true));
					add(new PrintItemObj("打印数据下划线", 8, false, PrintItemObj.ALIGN.LEFT, true));
					add(new PrintItemObj("打印数据下划线", 8, false, PrintItemObj.ALIGN.LEFT, true));
					add(new PrintItemObj("打印数据不换行测试打印数据不换行测试打印数据不换行测试", 8, false, PrintItemObj.ALIGN.LEFT, false,
							true));
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
					// add(new PrintItemObj("打印数据左边距测试",8,false,
					// PrintItemObj.ALIGN.LEFT,false,true,29,0,40));
					// add(new PrintItemObj("打印数据左边距测试",8,false,
					// PrintItemObj.ALIGN.LEFT,false,true,29,0,40));
					// add(new PrintItemObj("打印数据左边距测试",8,false,
					// PrintItemObj.ALIGN.LEFT,false,true,29,0,40));
				}
			}, new AidlPrinterListener.Stub() {
				@Override
				public void onPrintFinish() throws RemoteException {
					aidlPrinter.prnStart();
					aidlPrinter.printClose();
					String endTime = getCurTime();
					// showMessage(getResources().getString(R.string.print_end_time) + endTime);
				}

				@Override
				public void onError(int arg0) throws RemoteException {
					// showMessage(getResources().getString(R.string.print_error_code) + arg0);
				}
			});

		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	//
	/**
	 * 打印位图
	 * 
	 * @param v
	 * @createtor：Administrator
	 * @date:2015-8-4 下午2:39:33
	 */
	public void printBitmap(View v) {
		try {
			Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
			String startTime = getCurTime();
			// showMessage(getResources().getString(R.string.print_start_time) + startTime);
			aidlPrinter.printBmp(0, bitmap.getWidth(), bitmap.getHeight(), bitmap, new AidlPrinterListener.Stub() {
				@Override
				public void onPrintFinish() throws RemoteException {
					// aidlPrinter.prnStr("\r\n\r\n\r\n");
					aidlPrinter.prnStart();
					aidlPrinter.printClose();
					String endTime = getCurTime();
					// showMessage(getResources().getString(R.string.print_end_time) + endTime);
				}

				@Override
				public void onError(int arg0) throws RemoteException {
					// showMessage(getResources().getString(R.string.print_error_code) + arg0);
				}
			});
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	//
	private class PrintStateChangeListener extends AidlPrinterListener.Stub {

		@Override
		public void onError(int arg0) throws RemoteException {
			// showMessage(getResources().getString(R.string.print_error_code) + arg0);
		}

		@Override
		public void onPrintFinish() throws RemoteException {
			String endTime = getCurTime();
			// showMessage(getResources().getString(R.string.print_end_time) + endTime);

			aidlPrinter.prnStart();
			aidlPrinter.printClose();
		}

	}

	//
	/**
	 * 打印条码
	 * 
	 * @param v
	 * @createtor：Administrator
	 * @date:2015-8-4 下午3:02:21
	 */
	public void printBarCode(View v) {
		try {
			String startTime = getCurTime();
			// showMessage(getResources().getString(R.string.print_start_time) + startTime);
			aidlPrinter.printBarCode(300, 200, CODE_39, "1234", new PrintStateChangeListener());
			// aidlPrinter.printBarCode(-1, 162, 18, 65, "23418753401");
			// aidlPrinter.printBarCode(-1, 162, 18, 66, "03400471");
			// aidlPrinter.printBarCode(-1, 162, 18, 67, "2341875340111");
			// aidlPrinter.printBarCode(-1, 162, 18, 68, "23411875");
			// aidlPrinter.printBarCode(-1, 162, 18, 69, "*23418*");
			// aidlPrinter.printBarCode(-1, 162, 18, 70, "234187534011");
			// aidlPrinter.printBarCode(-1, 162, 18, 71, "23418");
			// aidlPrinter.printBarCode(-1, 162, 18, 72, "23418");
			// aidlPrinter.printBarCode(-1, 162, 18, 73, "{A23418");
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	//
	/**
	 * 设置打印灰度
	 * 
	 * @param v
	 * @createtor：Administrator
	 * @date:2015-8-4 下午3:02:27
	 */
	public void setPrintHGray(View v) {
		List<PrintItemObj> list = new ArrayList<>();
		list.add(new PrintItemObj("打印浓度设置为1000"));
		list.add(new PrintItemObj("打印浓度设置为1000"));
		list.add(new PrintItemObj("打印浓度设置为1000"));
		list.add(new PrintItemObj("打印浓度设置为1000"));
		list.add(new PrintItemObj("打印浓度设置为1000"));
		try {
			aidlPrinter.setPrinterGray(1000);
			aidlPrinter.printText(list, new AidlPrinterListener.Stub() {
				@Override
				public void onError(int i) throws RemoteException {
					// showMessage(getResources().getString(R.string.print_error_code) + i);
				}

				@Override
				public void onPrintFinish() throws RemoteException {
					aidlPrinter.prnStart();
					aidlPrinter.printClose();
					// showMessage(getResources().getString(R.string.print_success));
				}
			});
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	//
	public void setPrintLGray(View v) {
		List<PrintItemObj> list1 = new ArrayList<>();
		list1.add(new PrintItemObj("打印浓度设置为0"));
		list1.add(new PrintItemObj("打印浓度设置为0"));
		list1.add(new PrintItemObj("打印浓度设置为0"));
		list1.add(new PrintItemObj("打印浓度设置为0"));
		list1.add(new PrintItemObj("打印浓度设置为0"));
		try {
			aidlPrinter.setPrinterGray(0);
			aidlPrinter.printText(list1, new AidlPrinterListener.Stub() {
				@Override
				public void onError(int i) throws RemoteException {
					// showMessage(getResources().getString(R.string.print_error_code) + i);
				}

				@Override
				public void onPrintFinish() throws RemoteException {
					aidlPrinter.prnStart();
					aidlPrinter.printClose();
					// showMessage(getResources().getString(R.string.print_success));
				}
			});
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	//
	public void setPrintNGray(View v) {
		List<PrintItemObj> list1 = new ArrayList<>();
		list1.add(new PrintItemObj("打印浓度设置为500"));
		list1.add(new PrintItemObj("打印浓度设置为500"));
		list1.add(new PrintItemObj("打印浓度设置为500"));
		list1.add(new PrintItemObj("打印浓度设置为500"));
		list1.add(new PrintItemObj("打印浓度设置为500"));
		try {
			aidlPrinter.setPrinterGray(500);
			aidlPrinter.printText(list1, new AidlPrinterListener.Stub() {
				@Override
				public void onError(int i) throws RemoteException {

					// (getResources().getString(R.string.print_error_code) + i);
				}

				@Override
				public void onPrintFinish() throws RemoteException {
					aidlPrinter.prnStart();
					aidlPrinter.printClose();
					// showMessage(getResources().getString(R.string.print_success));
				}
			});
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	public void printBigBuy(View v) {
		try {
			String startTime = getCurTime();
			// showMessage(getResources().getString(R.string.print_start_time) + startTime);
			aidlPrinter.printText(new ArrayList<PrintItemObj>() {
				{
					add(new PrintItemObj("POS签购单", 16, true, PrintItemObj.ALIGN.CENTER));
					add(new PrintItemObj("商户号:00000000000 终端号:100000000", 8, true, PrintItemObj.ALIGN.LEFT));
					add(new PrintItemObj("操作员:01收单机构:中国银联", 8, true, PrintItemObj.ALIGN.LEFT));
					add(new PrintItemObj("卡号:6214444******0095  1", 8, true, PrintItemObj.ALIGN.LEFT));
					add(new PrintItemObj("卡组织:境内贷记卡", 8, true, PrintItemObj.ALIGN.LEFT));
					add(new PrintItemObj("卡类别:工商银行  有效期:20/12", 8, true, PrintItemObj.ALIGN.LEFT));
					add(new PrintItemObj("交易类别:消费", 16, true, PrintItemObj.ALIGN.LEFT));
					add(new PrintItemObj("批次号:000001", 8, true, PrintItemObj.ALIGN.LEFT));
					add(new PrintItemObj("凭证号:000033  授权号:000000", 8, true, PrintItemObj.ALIGN.LEFT));
					add(new PrintItemObj("参考号:1009000000033", 8, true, PrintItemObj.ALIGN.LEFT));
					add(new PrintItemObj("日期/时间:2017/10/10 11:11:11", 8, true, PrintItemObj.ALIGN.LEFT));
					add(new PrintItemObj("实付金额:RMB  100元", 16, true, PrintItemObj.ALIGN.LEFT));
					add(new PrintItemObj("订单金额:RMB  100元", 8, true, PrintItemObj.ALIGN.LEFT));
					add(new PrintItemObj("优惠金额:RMB  0.00元", 8, true, PrintItemObj.ALIGN.LEFT));
					add(new PrintItemObj("备注:", 8, true, PrintItemObj.ALIGN.LEFT));
					add(new PrintItemObj("AID:A000000333010101 TVR:008004600:", 8, true, PrintItemObj.ALIGN.LEFT));
					add(new PrintItemObj("ARQC:ABCDEFDGJHHHGA ATC:0020:", 8, true, PrintItemObj.ALIGN.LEFT));
					add(new PrintItemObj("便利支付  便利生活:", 8, true, PrintItemObj.ALIGN.LEFT));
					add(new PrintItemObj("持卡人签名:", 8, true, PrintItemObj.ALIGN.LEFT));
					add(new PrintItemObj("本人确认以上交易,同意将其记入本卡账户", 8, true, PrintItemObj.ALIGN.LEFT));
					add(new PrintItemObj("I ACKNOWLEDGE SATISFACTORY RECEIPT OF RELATIVE GOODS/SERVICES", 8, true,
							PrintItemObj.ALIGN.LEFT));
					add(new PrintItemObj("--------------------------------", 8, true, PrintItemObj.ALIGN.LEFT));
					add(new PrintItemObj("SD_V2.1.8.11404.0 客服热线:95016", 8, true, PrintItemObj.ALIGN.LEFT));
					add(new PrintItemObj("------------商户存根------------", 8, true, PrintItemObj.ALIGN.LEFT));
					add(new PrintItemObj("\n"));
				}
			}, new AidlPrinterListener.Stub() {

				@Override
				public void onPrintFinish() throws RemoteException {

					String endTime = getCurTime();
					// showMessage(getResources().getString(R.string.print_end_time) + endTime);
					aidlPrinter.prnStart();
					aidlPrinter.printClose();
				}

				@Override
				public void onError(int arg0) throws RemoteException {
					// showMessage(getResources().getString(R.string.print_error_code) + arg0);
				}
			});
		} catch (RemoteException e1) {
			e1.printStackTrace();
		}
	}

	//
	// private String getCurTime(){
	// Date date =new Date(System.currentTimeMillis());
	// SimpleDateFormat format =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
	// String time = format.format(date);
	// return time;
	// }
	//
	public void printSmBuy(View v) {
		try {
			String startTime = getCurTime();
			// (getResources().getString(R.string.print_start_time) + startTime);
			// Bitmap bitmap =
			// BitmapFactory.decodeResource(getResources(),R.drawable.pic_1);
			// aidlPrinter.printBmp(0, bitmap.getWidth(), bitmap.getHeight(), bitmap,
			// mListen);
			aidlPrinter.printText(new ArrayList<PrintItemObj>() {
				{
					add(new PrintItemObj("POS签购单", 8, true, PrintItemObj.ALIGN.CENTER));
					add(new PrintItemObj("商户号:00000000000 终端号:100000000", 4, true, PrintItemObj.ALIGN.LEFT));
					add(new PrintItemObj("操作员:01收单机构:CUP", 4, true, PrintItemObj.ALIGN.LEFT));
					add(new PrintItemObj("卡号:6214444******0095  1", 4, true, PrintItemObj.ALIGN.LEFT));
					add(new PrintItemObj("卡组织:境内贷记卡", 4, true, PrintItemObj.ALIGN.LEFT));
					add(new PrintItemObj("卡类别:工商银行  有效期:20/12", 4, true, PrintItemObj.ALIGN.LEFT));
					add(new PrintItemObj("交易类别:消费", 8, true, PrintItemObj.ALIGN.LEFT));
					add(new PrintItemObj("批次号:000001", 4, true, PrintItemObj.ALIGN.LEFT));
					add(new PrintItemObj("凭证号:000033  授权号:000000", 4, true, PrintItemObj.ALIGN.LEFT));
					add(new PrintItemObj("参考号:1009000000033", 4, true, PrintItemObj.ALIGN.LEFT));
					add(new PrintItemObj("日期/时间:2017/10/10 11:11:11", 4, true, PrintItemObj.ALIGN.LEFT));
					add(new PrintItemObj("实付金额:RMB  100元", 8, true, PrintItemObj.ALIGN.LEFT));
					add(new PrintItemObj("订单金额:RMB  100元", 4, true, PrintItemObj.ALIGN.LEFT));
					add(new PrintItemObj("优惠金额:RMB  0.00元", 4, true, PrintItemObj.ALIGN.LEFT));
					add(new PrintItemObj("备注:", 4, true, PrintItemObj.ALIGN.LEFT));
					add(new PrintItemObj("AID:A000000333010101 TVR:008004600:", 4, true, PrintItemObj.ALIGN.LEFT));
					add(new PrintItemObj("ARQC:ABCDEFDGJHHHGA ATC:0020:", 4, true, PrintItemObj.ALIGN.LEFT));
					add(new PrintItemObj("便利支付  便利生活:", 4, true, PrintItemObj.ALIGN.LEFT));
					add(new PrintItemObj("持卡人签名:", 4, true, PrintItemObj.ALIGN.LEFT));
					add(new PrintItemObj("本人确认以上交易,同意将其记入本卡账户", 4, true, PrintItemObj.ALIGN.LEFT));
					add(new PrintItemObj("I ACKNOWLEDGE SATISFACTORY RECEIPT OF RELATIVE GOODS/SERVICES", 4, true,
							PrintItemObj.ALIGN.LEFT));
					add(new PrintItemObj("------------------------------------------------", 4, true,
							PrintItemObj.ALIGN.LEFT));
					add(new PrintItemObj("SD_V2.1.8.11404.0 客服热线:95016", 4, true, PrintItemObj.ALIGN.LEFT));
					add(new PrintItemObj("--------------------商户存根--------------------", 4, true,
							PrintItemObj.ALIGN.LEFT));
					add(new PrintItemObj("\n"));
				}
			}, new AidlPrinterListener.Stub() {

				@Override
				public void onPrintFinish() throws RemoteException {
					String endTime = getCurTime();
					// showMessage(getResources().getString(R.string.print_end_time) + endTime);
					aidlPrinter.prnStart();
					aidlPrinter.printClose();
				}

				@Override
				public void onError(int arg0) throws RemoteException {
					// showMessage(getResources().getString(R.string.print_error_code) + arg0);
				}
			});
		} catch (RemoteException e1) {
			e1.printStackTrace();
		}
	}

	//
	public void printSymbol(View v) {
		try {
			String startTime = getCurTime();
			// showMessage(getResources().getString(R.string.print_start_time) + startTime);
			aidlPrinter.printText(new ArrayList<PrintItemObj>() {
				{
					add(new PrintItemObj("收款￥0.01元，退款￥0.00元", 8, true, PrintItemObj.ALIGN.LEFT));
					add(new PrintItemObj("收款$0.01元，退款$0.00元", 8, true, PrintItemObj.ALIGN.LEFT));
					add(new PrintItemObj("收款€0.01元，退款€0.00元", 8, true, PrintItemObj.ALIGN.LEFT));
					add(new PrintItemObj("收款￡0.01元，退款￡0.00元", 8, true, PrintItemObj.ALIGN.LEFT));
					add(new PrintItemObj("收款₣0.01元，退款₣0.00元", 8, true, PrintItemObj.ALIGN.LEFT));
					add(new PrintItemObj("收款¥0.01元，退款¥0.00元", 8, true, PrintItemObj.ALIGN.LEFT));
					add(new PrintItemObj("收款₩0.01元，退款₩0.00元", 8, true, PrintItemObj.ALIGN.LEFT));
					add(new PrintItemObj("\n"));
				}
			}, new AidlPrinterListener.Stub() {

				@Override
				public void onPrintFinish() throws RemoteException {
					String endTime = getCurTime();
					// showMessage(getResources().getString(R.string.print_end_time) + endTime);
					aidlPrinter.prnStart();
					aidlPrinter.printClose();
				}

				@Override
				public void onError(int arg0) throws RemoteException {
					// showMessage(getResources().getString(R.string.print_error_code) + arg0);
				}
			});
		} catch (RemoteException e1) {
			e1.printStackTrace();
		}
	}

	//
	public void printBitmaps(View v) throws RemoteException {
		try {

			Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.pic_1);

			final CountDownLatch countDownLatch = new CountDownLatch(Constant.COUNT_LATCH);
			aidlPrinter.printBmp(0, bitmap.getWidth(), bitmap.getHeight(), bitmap, new AidlPrinterListener.Stub() {
				@Override
				public void onPrintFinish() throws RemoteException {
					String endTime = getCurTime();
					// showMessage(getResources().getString(R.string.print_end_time) + endTime);
					countDownLatch.countDown();
				}

				@Override
				public void onError(int arg0) throws RemoteException {
					// showMessage(getResources().getString(R.string.print_error_code) + arg0);
				}
			});
			try {
				countDownLatch.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			// aidlPrinter.prnStart();
			// aidlPrinter.printClose();

			Bitmap bitmap1 = BitmapFactory.decodeResource(getResources(), R.drawable.pic_2);
			final CountDownLatch countDownLatch2 = new CountDownLatch(Constant.COUNT_LATCH);
			aidlPrinter.printBmp(0, bitmap1.getWidth(), bitmap1.getHeight(), bitmap1, new AidlPrinterListener.Stub() {

				@Override
				public void onPrintFinish() throws RemoteException {
					String endTime = getCurTime();
					// showMessage(getResources().getString(R.string.print_end_time) + endTime);
					countDownLatch2.countDown();
				}

				@Override
				public void onError(int arg0) throws RemoteException {
					// showMessage(getResources().getString(R.string.print_error_code) + arg0);
				}
			});
			try {
				countDownLatch2.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			aidlPrinter.prnStr("\n\n\n");
			aidlPrinter.prnStart();
			aidlPrinter.printClose();

			// Bitmap bitmap2 =
			// BitmapFactory.decodeResource(getResources(),R.drawable.pic_3);
			// aidlPrinter.printBmp(0, bitmap2.getWidth(), bitmap2.getHeight(), bitmap2,
			// mListen);

			//
			// aidlPrinter.prnStart();
			// aidlPrinter.printClose();

		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	AidlPrinterListener mListen = new AidlPrinterListener.Stub() {
		@Override
		public void onError(int i) throws RemoteException {
			// showMessage(getResources().getString(R.string.print_error_code) + i);
		}

		@Override
		public void onPrintFinish() throws RemoteException {
			aidlPrinter.prnStart();
			aidlPrinter.printClose();
			// showMessage(getResources().getString(R.string.print_success));
		}
	};

}
