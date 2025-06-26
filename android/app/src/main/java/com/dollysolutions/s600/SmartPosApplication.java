package com.dollysolutions.s600;

import android.app.Application;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.kp.ktsdkservice.card.AidlReadCard;
import com.kp.ktsdkservice.data.PsamConstant;
import com.kp.ktsdkservice.devicebasic.AidlBase;
import com.kp.ktsdkservice.emv.EMVProvider;
import com.kp.ktsdkservice.magcard.AidlMagCard;
import com.kp.ktsdkservice.pinpad.PinPadProvider;
import com.kp.ktsdkservice.printer.AidlPrinter;
import com.kp.ktsdkservice.psam.AidlPsam;
import com.kp.ktsdkservice.rfcard.AidlRFCard;
import com.kp.ktsdkservice.security.SecurityProvider;
import com.kp.ktsdkservice.service.AidlDeviceService;

import om.kp.ktsdkservice.iccard.AidlICCard;

public class SmartPosApplication extends Application {

	public static final String TAG = "SmartPosApplication";
	private static String PACKAGENAME = "com.kp.ktsdkservice";
	private static String CLASSNAME = "com.kp.ktsdkservice.service.DeviceService";

	public AidlPrinter printer;
	public AidlPsam psam;
	// public AidlMagCard magCard;
	public AidlRFCard aidlRFCard;
	public AidlICCard aidlICCard;
	public AidlBase aidlBase;
	public PinPadProvider pinPadProvider;
	public SecurityProvider securityProvider;
	public EMVProvider emvProvider;
	public AidlReadCard aidlReadCard;

	public AidlDeviceService serviceManager;

	public static SmartPosApplication instance;

	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;
		bindService();
	}

	public void bindService() {
		Intent intent = new Intent();
		intent.setClassName(PACKAGENAME, CLASSNAME);
		boolean flag = bindService(intent, conn, BIND_AUTO_CREATE);
		if (flag) {
			Log.d(TAG, "Binder success!");
		} else {
			Log.d(TAG, "Binder fail!");
		}
	}

	private ServiceConnection conn = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder serviceBinder) {
			if (serviceBinder != null) {
				Log.d(TAG, "aidlService service success!");
				serviceManager = AidlDeviceService.Stub.asInterface(serviceBinder);

				try {
					printer = AidlPrinter.Stub
							.asInterface(serviceManager.getPrinter());

					psam = AidlPsam.Stub
							.asInterface(serviceManager.getPSAMReader(PsamConstant.PSAM_DEV_ID_1));

					// magCard = AidlMagCard.Stub
					// .asInterface(serviceManager.getMagCardReader());

					// aidlRFCard = AidlRFCard.Stub
					// 		.asInterface(serviceManager.getRFIDReader());

					aidlICCard = AidlICCard.Stub
							.asInterface(serviceManager.getInsertCardReader());

					aidlBase = AidlBase.Stub
							.asInterface(serviceManager.getDeviceBasic());

					// pinPadProvider = PinPadProvider.Stub
					// 		.asInterface(serviceManager.getPinPad());

					securityProvider = SecurityProvider.Stub
							.asInterface(serviceManager.getSecurity());

					// emvProvider = EMVProvider.Stub
					// 		.asInterface(serviceManager.getEmv());

					aidlReadCard = AidlReadCard.Stub
							.asInterface(serviceManager.getCardReader());

				} catch (RemoteException e) {
					throw new RuntimeException(e);
				}

			}
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			Log.d(TAG, "AidlService  disconnected.");
		}
	};

	public static SmartPosApplication getInstance() {
		return instance;
	}

}
