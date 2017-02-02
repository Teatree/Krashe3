package com.uwsoft.editor.renderer.factory.component;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.uwsoft.editor.renderer.components.DimensionsComponent;
import com.uwsoft.editor.renderer.components.label.LabelComponent;
import com.uwsoft.editor.renderer.data.LabelVO;
import com.uwsoft.editor.renderer.data.MainItemVO;
import com.uwsoft.editor.renderer.data.ProjectInfoVO;
import com.uwsoft.editor.renderer.data.ResolutionEntryVO;
import com.uwsoft.editor.renderer.factory.EntityFactory;
import com.uwsoft.editor.renderer.resources.IResourceRetriever;

public class LabelComponentFactory extends ComponentFactory{
	
	private static int labelDefaultSize = 12;

	public LabelComponentFactory(PooledEngine engine, IResourceRetriever rm) {
        super(engine, rm);
	}

	@Override
	public void createComponents(Entity root, Entity entity, MainItemVO vo) {
		 createCommonComponents(entity, vo, EntityFactory.LABEL_TYPE);
		 createParentNodeComponent(root, entity);
		 createNodeComponent(root, entity);
		 createLabelComponent(entity, (LabelVO) vo);
	}

	@Override
	protected DimensionsComponent createDimensionsComponent(Entity entity, MainItemVO vo) {
        DimensionsComponent component =engine.createComponent(DimensionsComponent.class);
        component.height = ((LabelVO) vo).height;
        component.width = ((LabelVO) vo).width;

        entity.add(component);
        return component;
    }

    protected LabelComponent createLabelComponent(Entity entity, LabelVO vo) {
//    	LabelComponent component = new LabelComponent(vo.text, generateStyle(rm, vo.style, vo.size));
    	LabelComponent component = new LabelComponent(vo.text, generateStyle(rm, vo.style, vo.size, vo.tint));
        component.setText(vo.text);
        component.setStyle(generateStyle(rm, vo.style, vo.size, vo.tint));
        component.fontName = vo.style;
        component.fontSize = vo.size;
        component.setAlignment(vo.align);

        ProjectInfoVO projectInfoVO = rm.getProjectVO();
        ResolutionEntryVO resolutionEntryVO = rm.getLoadedResolution();
        float multiplier = resolutionEntryVO.getMultiplier(rm.getProjectVO().originalResolution);

        component.setFontScale(multiplier/projectInfoVO.pixelToWorld);

        entity.add(component);
        return component;
    }
    
    
    public static LabelStyle generateStyle(IResourceRetriever rManager, String fontName, int size, float[] tint) {

        if (size == 0) {
            size = labelDefaultSize;
        }
        return new LabelStyle(rManager.getBitmapFont(fontName, size), new Color(1,1,1,1));
    }

}
