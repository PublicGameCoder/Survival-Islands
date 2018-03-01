package managers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;

public class WorldProcessInteractor {

	private static WorldProcessInteractor instance;
	private World islandWorld;
	
	public static WorldProcessInteractor getManager() {
		if (instance == null) {
			instance = new WorldProcessInteractor();
		}
		return instance;
	}
	
	private WorldProcessInteractor() {
		
		loadNewVoidWorld("basicIslandsWorld");
	}
	
	private void loadNewVoidWorld(String worldName) {
		if (islandWorld != null) {
			deleteWorld(islandWorld.getWorldFolder());
		}
		World source = Bukkit.getWorld("VoidWorldBackup");
		if (source == null) {
			source = Bukkit.createWorld(new WorldCreator("VoidWorldBackup"));
		}
		unloadWorld(source);
		World target = Bukkit.createWorld(new WorldCreator(worldName));
		unloadWorld(target);
		copyWorld(source.getWorldFolder(),target.getWorldFolder());
		if (Bukkit.getWorld(worldName) == null) {
			Bukkit.createWorld(new WorldCreator(worldName));
		}
		islandWorld = Bukkit.getWorld(worldName);
	}
	
	public void unloadWorld(World world) {
	    if(world != null) {
	        Bukkit.getServer().unloadWorld(world, false);
	    }
	}
	
	private boolean deleteWorld(File path) {
	      if(path.exists()) {
	          File files[] = path.listFiles();
	          for(int i=0; i<files.length; i++) {
	              if(files[i].isDirectory()) {
	                  deleteWorld(files[i]);
	              } else {
	                  files[i].delete();
	              }
	          }
	      }
	      return(path.delete());
	}
	
	public void deleteByDisable() {
		deleteWorld(islandWorld.getWorldFolder());
	}
	
	public boolean copyWorld(File source, File target){
	    try {
	        ArrayList<String> ignore = new ArrayList<String>(Arrays.asList("uid.dat", "session.dat"));
	        if(!ignore.contains(source.getName())) {
	            if(source.isDirectory()) {
	                if(!target.exists())
	                target.mkdirs();
	                String files[] = source.list();
	                for (String file : files) {
	                    File srcFile = new File(source, file);
	                    File destFile = new File(target, file);
	                    copyWorld(srcFile, destFile);
	                }
	            } else {
	                InputStream in = new FileInputStream(source);
	                OutputStream out = new FileOutputStream(target);
	                byte[] buffer = new byte[1024];
	                int length;
	                while ((length = in.read(buffer)) > 0)
	                    out.write(buffer, 0, length);
	                in.close();
	                out.close();
	            }
	        }
	        return true;
	    } catch (IOException e) {
	    }
	    return false;
	}

	public World getIslandsWorld() {
		return this.islandWorld;
	}

}
