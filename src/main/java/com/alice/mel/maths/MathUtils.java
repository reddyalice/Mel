package com.alice.mel.maths;

import org.joml.Matrix4f;
import org.joml.Random;
import org.joml.Vector3f;

public class MathUtils {


    public static Random random = new Random();

    public static Matrix4f CreateTransformationMatrix(Vector3f translation, float rx, float ry, float rz, float s){
		Matrix4f matrix = new Matrix4f();
		matrix.identity().translate(translation).
         rotateX((float)Math.toRadians(rx)).
         rotateY((float)Math.toRadians(ry)).
         rotateZ((float)Math.toRadians(rz)).
         scale(s);
		return matrix;
	}
}
