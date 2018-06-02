package utilities;

import org.bukkit.Material;

public class ValuedMaterial {
	
	private Material material;
	private byte data;
	private float levelValue;
	
	public ValuedMaterial(Material mat, int data, float value) {
		this.material = mat;
		this.data = (byte)data;
		this.levelValue = value;
	}
	
	public Material getMaterial() {
		return material;
	}
	
	public byte getData() {
		return data;
	}

	public float getLevelValue() {
		return levelValue;
	}
}
