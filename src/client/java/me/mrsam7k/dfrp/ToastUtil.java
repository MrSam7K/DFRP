package me.mrsam7k.dfrp;

import dev.dfonline.flint.Flint;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.text.Text;

public class ToastUtil {

    //totally not stolen from flint (thanks)
    public static void toast(String title, String description) {
        Flint.getClient().getToastManager().add(SystemToast.create(Flint.getClient(), SystemToast.Type.UNSECURE_SERVER_WARNING, Text.literal(title), Text.literal(description)));
    }
}
