package managers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.primesoft.asyncworldedit.api.utils.IFuncParamEx;
import org.primesoft.asyncworldedit.api.worldedit.ICancelabeEditSession;

import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.world.registry.WorldData;

public class PasteAction implements IFuncParamEx<Integer, ICancelabeEditSession, MaxChangedBlocksException>{
        private final BukkitWorld bukkitWorld;
        private final Vector to;
        private final File file;

        public PasteAction(BukkitWorld bukkitWorld, Vector to, File file){
            this.bukkitWorld = bukkitWorld;
            this.to = to;
            this.file = file;
        }

        @Override
        public Integer execute(ICancelabeEditSession editSession) throws MaxChangedBlocksException {
            try {
                ClipboardReader reader = ClipboardFormat.SCHEMATIC.getReader(new FileInputStream(file));
                WorldData worldData = bukkitWorld.getWorldData();
                Clipboard clipboard = reader.read(worldData);
                ClipboardHolder holder = new ClipboardHolder(clipboard, worldData);
                editSession.enableQueue();
                editSession.setFastMode(true);
                //Vector to = new Vector(origin.getBlockX(), origin.getBlockY(), origin.getBlockZ());
                final Operation operation = holder.createPaste(editSession, worldData).to(to).build();
                Operations.completeBlindly(operation);
                editSession.flushQueue();
            } catch (IOException e) {
                System.out.println("Error trying to paste " + file.getName() + " async");
                e.printStackTrace();
            }
            return 32768;
        }
    }