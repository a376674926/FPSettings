
package cn.stj.settings.activity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import cn.stj.settings.R;
import cn.stj.settings.adapter.OpenOffListAdapter;
import cn.stj.settings.adapter.SettingListAdapter;
import cn.stj.settings.entity.LocaleInfo;
import cn.stj.settings.presenter.SettingPresenter;
import cn.stj.settings.presenter.SettingPresenterImpl;
import cn.stj.settings.utils.DateTimeUtil;

/**
 * ring setting activity
 * 
 * @author jackey
 */
public class LanguageSettingActivity extends BaseActivity implements
        BaseActivity.BottomKeyClickListener {

    private static final String TAG = LanguageSettingActivity.class.getSimpleName();
    private OpenOffListAdapter mSettingListAdapter;
    private int mSelectedPosition;
    // save the selected sleep time
    private static final String SELECTED_LANGUAGE = "selected_language";
    private static final boolean DEBUG = false;
    private HashMap<String, Integer> mLanguageSelectedMap = new HashMap<String, Integer>();
    private SettingPresenter mSettingPresenter;
    private String[] mLanguageNames;
    private LocaleInfo[] mLocaleInfos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAboveViewStub.setLayoutResource(R.layout.activity_settings_listview);
        setBottomKeyClickListener(this);

        initView();

        mSettingPresenter = new SettingPresenterImpl(this);
    }

    /**
     * 初始化系统设置界面
     */
    private void initView() {
        View listView = mAboveViewStub.inflate();
        mLanguageNames = getLocaleInfos();
        mSettingListAdapter = new OpenOffListAdapter(this, Arrays.asList(mLanguageNames));
        final ListView lv = (ListView) listView.findViewById(R.id.main_list_view);
        lv.setAdapter(mSettingListAdapter);
        lv.setOnItemClickListener(new ListItemClickListener());
        lv.setOnItemSelectedListener(new ListItemSelectedListener());

        updateLanguageListView();
    }

    private String getDefaultLanguageName() {
        Configuration conf = getResources().getConfiguration();
        String language = conf.locale.getLanguage();
        String languageName;
        if (language.equals("zz")) {
            String country = conf.locale.getCountry();
            if (country.equals("ZZ")) {
                languageName = "[Developer] Accented English (zz_ZZ)";
            } else if (country.equals("ZY")) {
                languageName = "[Developer] Fake Bi-Directional (zz_ZY)";
            } else {
                languageName = "";
            }
        } else if (language.equals("zh")) {
            languageName = getLocaleString(conf.locale);
        } else if (hasOnlyOneLanguageInstance(language,
                Resources.getSystem().getAssets().getLocales())) {
            languageName = conf.locale.getDisplayLanguage(conf.locale);
        } else {
            languageName = conf.locale.getDisplayName(conf.locale);
        }
        if (languageName.length() > 1) {
            languageName = toTitleCase(languageName);
        }
        return languageName;
    }

    private void updateLanguageListView() {
        String defaultLanguageName = getDefaultLanguageName();
        for (int i = 0; i < mLanguageNames.length; i++) {
            if (defaultLanguageName.equals(mLanguageNames[i])) {
                mSelectedPosition = i;
            }
        }
        mSettingListAdapter.updateOpenOffStatusMap(mSelectedPosition, true);
        mLanguageSelectedMap.put(SELECTED_LANGUAGE, mSelectedPosition);
        mSettingListAdapter.notifyDataSetChanged();
    }

    private class ListItemClickListener implements OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            mSelectedPosition = position;
            onSettingItemClick(position);
        }
    }

    private class ListItemSelectedListener implements OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position,
                long id) {
            mSelectedPosition = position;
            mSettingListAdapter.setSelectedItemPosition(position);
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
        }

    }

    private void onSettingItemClick(int position) {
        if (mLanguageSelectedMap.get(SELECTED_LANGUAGE) != null) {
            int selectedPosition = mLanguageSelectedMap.get(SELECTED_LANGUAGE);
            if (selectedPosition != position) {
                mSettingListAdapter.getmOpenOffStatusMap().put(selectedPosition, false);
            }
        }
        mSettingListAdapter.getmOpenOffStatusMap().put(position, true);
        mLanguageSelectedMap.put(SELECTED_LANGUAGE, position);
        mSettingListAdapter.setSelectedItemPosition(position);
    }

    @Override
    public void onLeftKeyPress() {
        confirm();
        finish();
    }

    private void confirm() {
        Locale locale = mLocaleInfos[mSelectedPosition].getLocale();
        setLanguage(locale);
    }

    private void setLanguage(Locale locale) {
        mSettingPresenter.setLanguage(locale);
        this.onBackPressed();
    }

    @Override
    public void onMiddleKeyPress() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onRightKeyPress() {
        launchActivity(OtherSettingActivity.class);
    }

    private String getLocaleString(Locale locale) {
        final String[] specialLocaleCodes = getSpecialLocaleCodes();
        final String[] specialLocaleNames = getSpecialLocaleNames();
        return getDisplayName(locale, specialLocaleCodes, specialLocaleNames);
    }

    private String getDisplayName(
            Locale locale, String[] specialLocaleCodes, String[] specialLocaleNames) {
        String code = locale.toString();

        for (int i = 0; i < specialLocaleCodes.length; i++) {
            if (specialLocaleCodes[i].equals(code)) {
                return specialLocaleNames[i];
            }
        }
        return locale.getDisplayName(locale);
    }

    public String[] getSpecialLocaleCodes() {
        final Resources resources = getResources();
        return resources.getStringArray(
                com.android.internal.R.array.special_locale_codes);
    }

    public String[] getSpecialLocaleNames() {
        final Resources resources = getResources();
        return resources.getStringArray(
                com.android.internal.R.array.special_locale_names);
    }

    public String[] getAvailableLocaleNames() {
        Locale[] locales = Locale.getAvailableLocales();
        String[] localeNames = new String[locales.length];
        for (int i = 0; i < locales.length; i++) {
            Locale locale = locales[i];
            localeNames[i] = locale.getDisplayName(locale);
        }
        return localeNames;
    }

    private boolean hasOnlyOneLanguageInstance(String languageCode, String[] locales) {
        int count = 0;
        for (String localeCode : locales) {
            if (localeCode.length() > 2
                    && localeCode.startsWith(languageCode)) {
                count++;
                if (count > 1) {
                    return false;
                }
            }
        }
        return count == 1;
    }

    private String[] getLocaleInfos() {
        Resources resources = getResources();
        ArrayList<String> localeList = new ArrayList<String>(Arrays.asList(
                Resources.getSystem().getAssets().getLocales()));
        String[] locales = new String[localeList.size()];
        locales = localeList.toArray(locales);
        String[] specialLocaleCodes = getSpecialLocaleCodes();
        String[] specialLocaleNames = getSpecialLocaleNames();
        Arrays.sort(locales);

        final int origSize = locales.length;
        final LocaleInfo[] preprocess = new LocaleInfo[origSize];
        int finalSize = 0;
        for (int i = 0; i < origSize; i++) {
            final String s = locales[i];
            final int len = s.length();
            if (len == 5) {
                String language = s.substring(0, 2);
                String country = s.substring(3, 5);
                final Locale l = new Locale(language, country);

                if (finalSize == 0) {
                    if (DEBUG) {
                        Log.v(TAG, "adding initial " + toTitleCase(l.getDisplayLanguage(l)));
                    }
                    preprocess[finalSize++] =
                            new LocaleInfo(toTitleCase(l.getDisplayLanguage(l)), l);
                } else {
                    // check previous entry:
                    // same language and a country -> upgrade to full name and
                    // insert ours with full name
                    // diff lang -> insert ours with lang-only name
                    if (preprocess[finalSize - 1].getLocale().getLanguage().equals(
                            language) &&
                            !preprocess[finalSize - 1].getLocale().getLanguage().equals("zz")) {
                        if (DEBUG) {
                            Log.v(TAG, "backing up and fixing " +
                                    preprocess[finalSize - 1].getLocale() + " to " +
                                    getDisplayName(preprocess[finalSize - 1].getLocale(),
                                            specialLocaleCodes, specialLocaleNames));
                        }
                        preprocess[finalSize - 1].setLabel(toTitleCase(
                                getDisplayName(preprocess[finalSize - 1].getLocale(),
                                        specialLocaleCodes, specialLocaleNames)));
                        if (DEBUG) {
                            Log.v(TAG, "  and adding " + toTitleCase(
                                    getDisplayName(l, specialLocaleCodes, specialLocaleNames)));
                        }
                        preprocess[finalSize++] =
                                new LocaleInfo(toTitleCase(
                                        getDisplayName(
                                                l, specialLocaleCodes, specialLocaleNames)), l);
                    } else {
                        String displayName;
                        if (s.equals("zz_ZZ")) {
                            displayName = "[Developer] Accented English";
                        } else if (s.equals("zz_ZY")) {
                            displayName = "[Developer] Fake Bi-Directional";
                        } else {
                            displayName = toTitleCase(l.getDisplayLanguage(l));
                        }
                        if (DEBUG) {
                            Log.v(TAG, "adding " + displayName);
                        }
                        preprocess[finalSize++] = new LocaleInfo(displayName, l);
                    }
                }
            }
        }

        mLocaleInfos = new LocaleInfo[finalSize];
        for (int i = 0; i < finalSize; i++) {
            mLocaleInfos[i] = preprocess[i];
        }
        Arrays.sort(mLocaleInfos);
        String[] languageNames = new String[mLocaleInfos.length];
        for (int i = 0; i < mLocaleInfos.length; i++) {
            languageNames[i] = mLocaleInfos[i].getLabel();
        }
        return languageNames;
    }

    private static String toTitleCase(String s) {
        if (s.length() == 0) {
            return s;
        }
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }
}
