
package android.databinding;
import com.smartbracelet.sunny.sunnydemo2.BR;
class DataBinderMapper {
    final static int TARGET_MIN_SDK = 14;
    public DataBinderMapper() {
    }
    public android.databinding.ViewDataBinding getDataBinder(android.databinding.DataBindingComponent bindingComponent, android.view.View view, int layoutId) {
        switch(layoutId) {
                case com.smartbracelet.sunny.sunnydemo2.R.layout.activity_data_binding:
                    return com.smartbracelet.sunny.sunnydemo2.databinding.ActivityDataBindingBinding.bind(view, bindingComponent);
        }
        return null;
    }
    android.databinding.ViewDataBinding getDataBinder(android.databinding.DataBindingComponent bindingComponent, android.view.View[] views, int layoutId) {
        switch(layoutId) {
        }
        return null;
    }
    int getLayoutId(String tag) {
        if (tag == null) {
            return 0;
        }
        final int code = tag.hashCode();
        switch(code) {
            case 1514339820: {
                if(tag.equals("layout/activity_data_binding_0")) {
                    return com.smartbracelet.sunny.sunnydemo2.R.layout.activity_data_binding;
                }
                break;
            }
        }
        return 0;
    }
    String convertBrIdToString(int id) {
        if (id < 0 || id >= InnerBrLookup.sKeys.length) {
            return null;
        }
        return InnerBrLookup.sKeys[id];
    }
    private static class InnerBrLookup {
        static String[] sKeys = new String[]{
            "_all"
            ,"user"};
    }
}