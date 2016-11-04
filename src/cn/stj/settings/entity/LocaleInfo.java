package cn.stj.settings.entity;

import java.text.Collator;
import java.util.Locale;

public class LocaleInfo implements Comparable<LocaleInfo> {
    private Collator sCollator = Collator.getInstance();

    private String label;
    private Locale locale;
    
    public LocaleInfo(String label, Locale locale) {
        super();
        this.label = label;
        this.locale = locale;
    }
    public String getLabel() {
        return label;
    }
    public void setLabel(String label) {
        this.label = label;
    }
    public Locale getLocale() {
        return locale;
    }
    public void setLocale(Locale locale) {
        this.locale = locale;
    }
    
    @Override
    public String toString() {
        return this.label ;
    }
    
    @Override
    public int compareTo(LocaleInfo another) {
        return sCollator.compare(this.label, another.label);
    }
}