package com.z3db0y.flagship;

import androidx.annotation.NonNull;

public class Movement {
	public Type type;
	public double value;

	public enum Type {
		ROTATIONAL,
		LINEAR;
	}

	public Movement(@NonNull Type type, double value) {
		this.type = type;
		this.value = value;
	}
}
