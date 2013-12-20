package com.android.singaporeanorderingsystem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.R;
import com.android.bean.GetPTakeNumBean;
import com.android.bean.GetPayDetailBean;
import com.android.bean.LoginUserBean;
import com.android.common.AndroidPrinter;
import com.android.common.Constants;
import com.android.common.MyApp;
import com.android.component.ActivityComponent;
import com.android.component.LanguageComponent;
import com.android.component.StringResComponent;
import com.android.component.ui.MenuComponent;
import com.android.dao.DailyMoneyDao;
import com.android.dao.GetTakeNumDao;
import com.android.dao.NumListDao;
import com.android.dao.PayListDao;
import com.android.dao.UserDao2;
import com.android.dao.getDetailPayListDao;
import com.android.dialog.DialogBuilder;
import com.android.handler.RemoteDataHandler;
import com.android.handler.RemoteDataHandler.Callback;
import com.android.mapping.FoodMapping;
import com.android.model.ResponseData;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.App;
import com.googlecode.androidannotations.annotations.Bean;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.Fullscreen;
import com.googlecode.androidannotations.annotations.NoTitle;
import com.googlecode.androidannotations.annotations.ViewById;

//不需要标题
@NoTitle
// 全屏显示
@Fullscreen
// 绑定登录的layout
@EActivity(R.layout.setting)
public class SettingActivity extends BasicActivity {

	@ViewById(R.id.language_set)
	EditText language_set;

	@ViewById(R.id.print_one_edit)
	EditText print_one_edit;

	@ViewById(R.id.shop_set)
	EditText shop_set;

	@ViewById(R.id.take_price_edit)
	EditText take_price_edit;

	@ViewById(R.id.synchronization_menu_brn)
	Button synchronization_menu;

	@ViewById(R.id.synchronization_shop_brn)
	Button synchronization_shop;

	@ViewById(R.id.btu_discount)
	Button btu_discount;

	@ViewById(R.id.synchronization_pay_brn)
	Button synchronization_pay;

	@ViewById(R.id.price_set_brn)
	Button price_set_brn;

	@ViewById(R.id.print_one_btu)
	Button print_one_btu;

	@ViewById(R.id.btu_setting_all_tong)
	Button btu_setting_all_tong;

	@ViewById(R.id.admin_set)
	TextView admin_set;

	@ViewById(R.id.r_set_admin_lay)
	RelativeLayout r_set_admin_lay;

	@ViewById(R.id.layout_exit)
	RelativeLayout layout_exit;

	@ViewById(R.id.edit_setting_chongzhi_login_name)
	EditText edit_setting_chongzhi_login_name;

	@ViewById(R.id.btu_setting_login_name)
	Button btu_setting_login_name;

	@ViewById(R.id.edit_setting_chongzhi_login_password)
	EditText edit_setting_chongzhi_login_password;

	@ViewById(R.id.btu_setting_login_password)
	Button btu_setting_login_password;

	@ViewById(R.id.edit_setting_time)
	EditText edit_setting_time;

	@ViewById(R.id.btu_setting_time)
	Button btu_setting_time;

	@ViewById(R.id.setting_time)
	RelativeLayout setting_time;

	@ViewById(R.id.synchronizeText)
	TextView synchronize;

	@ViewById(R.id.menu_btn)
	ImageView menu;

	@Bean
	ActivityComponent activityComponent;

	@Bean
	StringResComponent stringResComponent;

	@Bean
	LanguageComponent languageComponent;

	@App
	MyApp myApp;

	@Bean
	MenuComponent menuComponent;

	@Bean
	AndroidPrinter androidPrinter;

	private MyProcessDialog dialog;
	private String search_date;
	private boolean is_chinese;
	private SharedPreferences sharedPrefs;
	public static String type;

	private class SyncALlOperation extends AsyncTask<String, Void, Integer> {

		@Override
		protected Integer doInBackground(String... objs) {
			// if(!isLatestData()){
			dialog.show();
			post_payList();
			post_numList();
			post_dailyMoney();
			dialog.cancel();
			if (!isLatestData()) {
				return -1;
			} else {

				return 1;
			}
			// }
			// return 0;
		}

		@Override
		protected void onPostExecute(Integer result) {
			dialog.dismiss();
			switch (result) {
			case 0:
				Toast.makeText(SettingActivity.this, getString(R.string.no_need_sync), Toast.LENGTH_SHORT).show();
				break;
			case 1:
				synchronize.setText(getString(R.string.sync_succ));
				Toast.makeText(SettingActivity.this, getString(R.string.sync_succ), Toast.LENGTH_SHORT).show();
				break;
			case -1:
				synchronize.setText(getString(R.string.sync_err));
				Toast.makeText(SettingActivity.this, getString(R.string.sync_err), Toast.LENGTH_SHORT).show();
				break;
			}
		}

		@Override
		protected void onPreExecute() {
			dialog.show();
		}

		@Override
		protected void onProgressUpdate(Void... values) {
		}
	}

	@AfterViews
	public void init() {
		// StrictMode.setThreadPolicy(new
		// StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork()
		// // 这里可以替换为detectAll()
		// // 就包括了磁盘读写和网络I/O
		// .penaltyLog() // 打印logcat，当然也可以定位到dropbox，通过文件保存相应的log
		// .build());
		// StrictMode.setVmPolicy(new
		// StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects() //
		// 探测SQLite数据库操作
		// .penaltyLog() // 打印logcat
		// .penaltyDeath().build());
		// m=new MyOrientationDetector3(SettingActivity.this);
		dialog = new MyProcessDialog(this, stringResComponent.dialogSet);
		Intent intent = this.getIntent();
		Bundle bundle = intent.getExtras();
		type = bundle.getString("type");
		edit_setting_time.setText(myApp.getSetting_time() / (60 * 1000) + "");

		btu_setting_time.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String set_time = edit_setting_time.getText().toString();
				if (set_time != null && !set_time.equals("") && !set_time.equals("null")) {
					Toast.makeText(SettingActivity.this, "设置成功", 1).show();
					myApp.setSetting_time(Long.parseLong(set_time) * 60 * 1000);
				}
			}
		});

		/** 判断今天是否是最新的 */
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		String date = df.format(new Date());
		SettingActivity.this.search_date = date;
		if (!isLatestData()) {
			synchronize.setText(getString(R.string.sync_err));
		} else {
			synchronize.setText(getString(R.string.sync_succ));
		}
		/***/
		price_set_brn = (Button) this.findViewById(R.id.price_set_brn);
		sharedPrefs = getSharedPreferences("language", Context.MODE_PRIVATE);
		String type = sharedPrefs.getString("type", "");
		if (myApp.getU_type().equals("SUPERADMIN")) {
			admin_set.setVisibility(View.VISIBLE);
			r_set_admin_lay.setVisibility(View.VISIBLE);
			setting_time.setVisibility(View.VISIBLE);
		}

		btu_setting_login_name.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String login_name = edit_setting_chongzhi_login_name.getText().toString();
				UserDao2 dao = UserDao2.getInatance(SettingActivity.this);
				ArrayList<LoginUserBean> datas = dao.getList(login_name);
				LoginUserBean user = new LoginUserBean();
				if (datas != null && datas.size() != 0) {
					user = datas.get(0);
					edit_setting_chongzhi_login_password.setText(user.getPasswrod());
					Toast.makeText(SettingActivity.this, "该用户确认成功", 1).show();
				} else {
					Toast.makeText(SettingActivity.this, "该用户不存在，请输入正确的用户", 1).show();
				}
			}
		});

		btu_setting_login_password.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String login_password = edit_setting_chongzhi_login_password.getText().toString();
				String login_name = edit_setting_chongzhi_login_name.getText().toString();
				if (login_name != null && !login_name.equals("")) {
					if (login_password != null && !login_name.equals("")) {
						UserDao2 dao = UserDao2.getInatance(SettingActivity.this);
						int reuslt = dao.update_password(login_name, login_password);
						if (reuslt == 1) {
							Toast.makeText(SettingActivity.this, "修改密码成功", 1).show();
						} else {
							Toast.makeText(SettingActivity.this, "修改密码失败，稍后重试", 1).show();
						}
					} else {
						Toast.makeText(SettingActivity.this, "密码不能为空", 1).show();
					}
				} else {
					Toast.makeText(SettingActivity.this, "用户名不能为空", 1).show();
				}
			}
		});

		btu_setting_all_tong.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new SyncALlOperation().execute("");
			}
		});

		menu.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				menuComponent.initPopupWindow();
			}
		});
		print_one_edit.setText(myApp.getIp_str());
		take_price_edit.setText(myApp.getDiscount());
		shop_set.setText(myApp.getSettingShopId());
		if (type == null) {
			type = "en";
		}
		if (type.equals("zh")) {
			is_chinese = true;
		} else {
			is_chinese = false;
		}
		if (!is_chinese) {
			language_set.setText("English");
		} else {
			language_set.setText("中文");
		}
		layout_exit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				CreatedDialog().create().show();
			}
		});
		print_one_btu.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String ip = print_one_edit.getText().toString();
				myApp.setIp_str(ip);
				androidPrinter.reconnect();
				Toast.makeText(SettingActivity.this, getString(R.string.toast_setting_succ), Toast.LENGTH_SHORT).show();
			}
		});
		btu_discount.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String text_discount = take_price_edit.getText().toString();
				myApp.setDiscount(text_discount);
				Toast.makeText(SettingActivity.this, getString(R.string.toast_setting_succ), Toast.LENGTH_SHORT).show();
			}
		});

		// 设置摊位ID,第一次超管必须设置好
		synchronization_shop.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String shop_id = shop_set.getText().toString();
				myApp.setSettingShopId(shop_id);
				Toast.makeText(SettingActivity.this, getString(R.string.toast_setting_succ), Toast.LENGTH_SHORT).show();
			}
		});

		// 语言设置
		language_set.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(SettingActivity.this, getString(R.string.toast_setting_language_succ), Toast.LENGTH_SHORT).show();
				DialogBuilder builder = new DialogBuilder(SettingActivity.this);
				builder.setTitle(R.string.message_title);
				builder.setMessage(R.string.message_2);
				builder.setPositiveButton(R.string.message_ok, new android.content.DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						// Toast.makeText(DailyPayActivity.this,
						// "你点击了确定",
						// Toast.LENGTH_SHORT).show();
						if (!is_chinese) {
							updateLange(Locale.SIMPLIFIED_CHINESE);
							language_set.setText("中文");
							Editor editor = sharedPrefs.edit();
							editor.putString("type", "zh");
							editor.commit();
							is_chinese = true;
						} else {
							updateLange(Locale.ENGLISH);
							language_set.setText("English");
							Editor editor = sharedPrefs.edit();
							editor.putString("type", "en");
							editor.commit();
							is_chinese = false;
						}
						getDetailPayListDao.getInatance(SettingActivity.this).delete();
						RemoteDataHandler.asyncGet(Constants.URL_PAY_DETAIL + myApp.getSettingShopId(), new Callback() {
							@Override
							public void dataLoaded(ResponseData data) {
								if (data.getCode() == 1) {
									String json = data.getJson();
									Log.e("返回数据", json);
									Log.e("中英文", is_chinese + "");
									ArrayList<GetPayDetailBean> datas = GetPayDetailBean.newInstanceList(json, is_chinese);
									Log.e("支付页详情数据", datas.size() + "");
									for (int i = 0; i < datas.size(); i++) {
										GetPayDetailBean bean = datas.get(i);
										getDetailPayListDao.getInatance(SettingActivity.this).save(bean.getId(), bean.getName(),
												bean.getNameZh());
									}
								} else if (data.getCode() == 0) {
									Toast.makeText(SettingActivity.this, "支付页失败", Toast.LENGTH_SHORT).show();
								} else if (data.getCode() == -1) {
									Toast.makeText(SettingActivity.this, getString(R.string.login_service_err), Toast.LENGTH_SHORT).show();
								}
							}
						});
					}
				});
				builder.setNegativeButton(R.string.message_cancle, new android.content.DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						// Toast.makeText(DailyPayActivity.this,
						// "你点击了取消",
						// Toast.LENGTH_SHORT).show();
					}
				});
				builder.create().show();

			}
		});

		// 支付款同步
		synchronization_pay.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				dialog.show();
				getDetailPayListDao.getInatance(SettingActivity.this).delete();
				RemoteDataHandler.asyncGet(Constants.URL_PAY_DETAIL + myApp.getSettingShopId(), new Callback() {
					@Override
					public void dataLoaded(ResponseData data) {
						if (data.getCode() == 1) {
							String json = data.getJson();
							Log.e("返回数据", json);
							Log.e("中英文", is_chinese + "");
							ArrayList<GetPayDetailBean> datas = GetPayDetailBean.newInstanceList(json, is_chinese);
							Log.e("支付页详情数据", datas.size() + "");
							for (int i = 0; i < datas.size(); i++) {
								GetPayDetailBean bean = datas.get(i);
								getDetailPayListDao.getInatance(SettingActivity.this).save(bean.getId(), bean.getName(), bean.getNameZh());
							}
							dialog.cancel();
							Toast.makeText(SettingActivity.this, getString(R.string.toast_setting_succ), Toast.LENGTH_SHORT).show();
						} else if (data.getCode() == 0) {
							Toast.makeText(SettingActivity.this, "支付页失败", Toast.LENGTH_SHORT).show();
						} else if (data.getCode() == -1) {
							Toast.makeText(SettingActivity.this, getString(R.string.login_service_err), Toast.LENGTH_SHORT).show();
						}
					}
				});
			}
		});

		// 现金配置
		price_set_brn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.show();
				GetTakeNumDao.getInatance(SettingActivity.this).delete();
				RemoteDataHandler.asyncGet(Constants.URL_TAKE_DNUM + myApp.getSettingShopId(), new Callback() {
					@Override
					public void dataLoaded(ResponseData data) {
						if (data.getCode() == 1) {
							String json = data.getJson();
							Log.e("金额配置返回数据", json);
							ArrayList<GetPTakeNumBean> datas = GetPTakeNumBean.newInstanceList(json, is_chinese);
							Log.e("金额配置详情数据", datas.size() + "");
							for (int i = 0; i < datas.size(); i++) {
								GetPTakeNumBean bean = datas.get(i);
								GetTakeNumDao.getInatance(SettingActivity.this).save(bean.getId(), bean.getPrice());
							}
							dialog.cancel();
							Toast.makeText(SettingActivity.this, getString(R.string.toast_setting_succ), Toast.LENGTH_SHORT).show();
						} else if (data.getCode() == 0) {
							Toast.makeText(SettingActivity.this, "金额配置失败", Toast.LENGTH_SHORT).show();
						} else if (data.getCode() == -1) {
							Toast.makeText(SettingActivity.this, getString(R.string.login_service_err), Toast.LENGTH_SHORT).show();
						}
					}
				});
			}
		});

		init_wifiReceiver();
	}

	// 同步菜单
	@Click({ R.id.synchronization_menu_brn })
	void foodSync() {
		System.out.println("myApp.getSettingShopId()-->" + myApp.getSettingShopId());
		dialog.show();
		if (myApp.getSettingShopId() == null || myApp.getSettingShopId().equals("") || myApp.getSettingShopId().equals("null")
				|| myApp.getSettingShopId().equals("0")) {
			Toast.makeText(SettingActivity.this, getString(R.string.setting_tanwei_id), Toast.LENGTH_SHORT).show();
			return;
		}
		String url = Constants.URL_FOODSLIST_PATH + myApp.getSettingShopId();

		FoodMapping.getJSONAndSave(url);

		Toast.makeText(SettingActivity.this, getString(R.string.toast_setting_succ), Toast.LENGTH_SHORT).show();
		dialog.cancel();

	}

	/***********************************************************************************/

	/* 判断今天是否已经是最新数据 */
	public boolean isLatestData() {
		List<Map<String, String>> pays = PayListDao.getInatance(this).getList(search_date);
		if (!pays.isEmpty()) {
			return false;
		}
		List<Map<String, String>> nums = NumListDao.getInatance(this).getList(search_date);
		if (!nums.isEmpty()) {
			return false;
		}
		HashMap<String, String> params = DailyMoneyDao.getInatance(SettingActivity.this).getList(search_date);
		if (!params.isEmpty()) {
			return false;
		}
		return true;
	}

	/* 提交每日支付 */
	public void post_payList() {
		try {
			HashMap<String, String> params = new HashMap<String, String>();
			List<Map<String, String>> datas = PayListDao.getInatance(this).getList(search_date);
			if (!datas.isEmpty()) {
				for (int i = 0; i < datas.size(); i++) {
					if (datas.get(i).get("type").equals("0")) {
						params.put("consumeTransactions[" + i + "].androidId", datas.get(i).get("android_id"));
						Log.e("consumeTransactions[" + i + "].androidId", datas.get(i).get("android_id"));
						params.put("consumeTransactions[" + i + "].consumption.id", datas.get(i).get("consumption_id"));
						Log.e("consumeTransactions[" + i + "].consumption.id", datas.get(i).get("consumption_id"));
						params.put("consumeTransactions[" + i + "].shop.id", datas.get(i).get("shop_id"));
						Log.e("consumeTransactions[" + i + "].shop.id", datas.get(i).get("shop_id"));
						params.put("consumeTransactions[" + i + "].user.id", datas.get(i).get("user_id"));
						Log.e("consumeTransactions[" + i + "].user.id", datas.get(i).get("user_id"));
						params.put("consumeTransactions[" + i + "].price", datas.get(i).get("price"));
						Log.e("consumeTransactions[" + i + "].price", datas.get(i).get("price"));
					}
				}

				ResponseData data = RemoteDataHandler.post(Constants.URL_POST_PAYLIST, params);
				if (data.getCode() == 1) {
					String json = data.getJson();
					// Toast.makeText(SettingActivity.this,
					// getString(R.string.toast_submmit_succ)+json,
					// Toast.LENGTH_SHORT).show();
					String str = json.substring(1, json.length() - 1);
					String[] array = str.split(",");
					if (array.length != 0) {
						for (int i = 0; i < array.length; i++) {
							Log.e("数据组", array[i] + "");
							int result = PayListDao.getInatance(SettingActivity.this).update_type(array[i], "1");
							if (result == -1) {
								// Toast.makeText(DailyPayActivity.this,
								// "每日支付接口更新失败", Toast.LENGTH_SHORT).show();
							} else {
								// Toast.makeText(DailyPayActivity.this,
								// "每日支付接口更新成功", Toast.LENGTH_SHORT).show();
							}

						}
					}
				} else if (data.getCode() == 0) {
					// Toast.makeText(SettingActivity.this,
					// getString(R.string.toast_submmit_fail),
					// Toast.LENGTH_SHORT).show();
				} else if (data.getCode() == -1) {
					// Toast.makeText(SettingActivity.this,
					// getString(R.string.toast_submmit_err),
					// Toast.LENGTH_SHORT).show();
				}
			}

		} catch (Exception e) {
			e.getMessage();
		}
	}

	/* 提交带回总数 */
	public void post_numList() {
		try {
			HashMap<String, String> params = new HashMap<String, String>();
			List<Map<String, String>> datas = NumListDao.getInatance(this).getList(search_date);
			if (!datas.isEmpty()) {
				for (int i = 0; i < datas.size(); i++) {
					if (datas.get(i).get("type").equals("0")) {
						params.put("cashTransactions[" + i + "].androidId", datas.get(i).get("android_id"));
						Log.e("cashTransactions[" + i + "].androidId", datas.get(i).get("android_id"));
						params.put("cashTransactions[" + i + "].cash.id", datas.get(i).get("cash_id"));
						Log.e("cashTransactions[" + i + "].cash.id", datas.get(i).get("cash_id"));
						params.put("cashTransactions[" + i + "].shop.id", datas.get(i).get("shop_id"));
						Log.e("cashTransactions[" + i + "].shop.id", datas.get(i).get("shop_id"));
						params.put("cashTransactions[" + i + "].user.id", datas.get(i).get("user_id"));
						Log.e("cashTransactions[" + i + "].user.id", datas.get(i).get("user_id"));
						params.put("cashTransactions[" + i + "].quantity", datas.get(i).get("quantity"));
						Log.e("cashTransactions[" + i + "].quantity", datas.get(i).get("quantity"));
					}
				}
			}

			ResponseData data = RemoteDataHandler.post(Constants.URL_POST_TAKENUM, params);

			if (data.getCode() == 1) {
				String json = data.getJson();
				// Toast.makeText(SettingActivity.this,
				// getString(R.string.toast_submmit_succ)+json,
				// Toast.LENGTH_SHORT).show();
				String str = json.substring(1, json.length() - 1);
				String[] array = str.split(",");
				if (array.length != 0) {
					for (int i = 0; i < array.length; i++) {
						Log.e("数据组", array[i] + "");
						int result = NumListDao.getInatance(SettingActivity.this).update_type(array[i], "1");
						if (result == -1) {
							// Toast.makeText(DailyPayActivity.this,
							// "带回总数接口更新失败", Toast.LENGTH_SHORT).show();
						} else {
							// Toast.makeText(DailyPayActivity.this,
							// "带回总数接口更新成功", Toast.LENGTH_SHORT).show();
						}
					}
				}
			} else if (data.getCode() == 0) {
				// Toast.makeText(SettingActivity.this,
				// getString(R.string.toast_submmit_fail),
				// Toast.LENGTH_SHORT).show();
			} else if (data.getCode() == -1) {
				// Toast.makeText(SettingActivity.this,
				// getString(R.string.toast_submmit_err),
				// Toast.LENGTH_SHORT).show();
			}

		} catch (Exception e) {
			e.getMessage();
		}
	}

	/* 提交每日营业额 */
	public void post_dailyMoney() {
		try {
			HashMap<String, String> params = DailyMoneyDao.getInatance(SettingActivity.this).getList(search_date);
			ResponseData data = RemoteDataHandler.post(Constants.URL_POST_DAILY_MONEY, params);
			if (data.getCode() == 1) {
				String json = data.getJson();
				// Toast.makeText(SettingActivity.this,
				// getString(R.string.toast_submmit_succ)+json,
				// Toast.LENGTH_SHORT).show();
				int result = DailyMoneyDao.getInatance(SettingActivity.this).update_type(search_date);
				if (result == -1) {
					// Toast.makeText(SettingActivity.this, "每日营业额更新失败",
					// Toast.LENGTH_SHORT).show();
				} else {
					// Toast.makeText(SettingActivity.this, "每日营业额更新成功",
					// Toast.LENGTH_SHORT).show();
				}
			} else if (data.getCode() == 0) {
				// Toast.makeText(SettingActivity.this,
				// getString(R.string.toast_submmit_fail),
				// Toast.LENGTH_SHORT).show();
			} else if (data.getCode() == -1) {
				// Toast.makeText(SettingActivity.this,
				// getString(R.string.toast_submmit_err),
				// Toast.LENGTH_SHORT).show();
			}

		} catch (Exception e) {
			e.getMessage();
		}
	}

	/*********************************************************************************/
	private void updateLange(Locale locale) {
		languageComponent.updateLanguage(SettingActivity_.class, type, locale);

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(print_one_edit.getWindowToken(), 0); // 强制隐藏键盘
		imm.hideSoftInputFromWindow(shop_set.getWindowToken(), 0); // 强制隐藏键盘
		imm.hideSoftInputFromWindow(take_price_edit.getWindowToken(), 0); // 强制隐藏键盘
		return super.onTouchEvent(event);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (type.equals("1")) {
				Intent intent = new Intent();
				intent.setClass(this, MainActivity.class);
				startActivity(intent);
				this.finish();
			} else {
				Intent intent = new Intent();
				intent.setClass(this, DailyPayActivity.class);
				startActivity(intent);
				this.finish();
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	// @Override
	// protected void onResume() {
	// super.onResume();
	// m.enable();
	// }
	// @Override
	// protected void onPause() {
	// super.onPause();
	// m.disable();
	// }
}
