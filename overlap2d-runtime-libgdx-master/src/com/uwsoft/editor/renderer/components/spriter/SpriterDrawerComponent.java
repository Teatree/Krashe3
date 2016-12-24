package com.uwsoft.editor.renderer.components.spriter;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;
import com.uwsoft.editor.renderer.utils.LibGdxDrawer;

public class SpriterDrawerComponent implements Component, Pool.Poolable{
	public LibGdxDrawer drawer;

	@Override
	public void reset() {

	}
}
