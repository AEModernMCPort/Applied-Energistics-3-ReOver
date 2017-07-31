package com.owens.oobjloader.parser;

import net.minecraft.util.ResourceLocation;

import java.io.IOException;
import java.io.InputStream;

public interface ResourceHelper {

	InputStream getInputStream(ResourceLocation location) throws IOException;

}
