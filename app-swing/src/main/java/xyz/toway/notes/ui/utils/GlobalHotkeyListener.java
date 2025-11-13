package xyz.toway.notes.ui.utils;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;

import java.util.HashSet;
import java.util.Set;

public class GlobalHotkeyListener implements NativeKeyListener {

    private final Set<Integer> pressed = new HashSet<>();
    private final Runnable onHotkey;

    public GlobalHotkeyListener(Runnable onHotkey) {
        this.onHotkey = onHotkey;
    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent e) {
        pressed.add(e.getKeyCode());

        boolean ctrl = pressed.contains(NativeKeyEvent.VC_CONTROL);
        boolean shift = pressed.contains(NativeKeyEvent.VC_SHIFT);
        boolean f1 = pressed.contains(NativeKeyEvent.VC_F1);

        if (ctrl && shift && f1) {
            onHotkey.run();
        }
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent e) {
        pressed.remove(e.getKeyCode());
    }

    @Override
    public void nativeKeyTyped(NativeKeyEvent e) {
    }

    public static void register(Runnable onHotkey) throws NativeHookException {
        GlobalScreen.registerNativeHook();
        GlobalScreen.addNativeKeyListener(new GlobalHotkeyListener(onHotkey));
    }
}
