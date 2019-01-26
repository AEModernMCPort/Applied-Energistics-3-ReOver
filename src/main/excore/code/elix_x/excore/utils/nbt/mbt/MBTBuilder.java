/*******************************************************************************
 * Copyright 2016 Elix_x
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package code.elix_x.excore.utils.nbt.mbt;

import java.util.ArrayList;
import java.util.List;

import code.elix_x.excore.utils.nbt.mbt.encoders.NBTClassEncoder;

public class MBTBuilder {

	private List<NBTEncoder> encoders = new ArrayList<NBTEncoder>();

	public MBTBuilder(){

	}

	public MBTBuilder add(NBTEncoder encoder){
		encoders.add(encoder);
		return this;
	}

	public MBTBuilder addDefaultEncoders(){
		for(NBTEncoder encoder : MBT.DEFAULTSPECIFICENCODERS){
			add(encoder);
		}
		return this;
	}

	@Deprecated
	public MBTBuilder addClassEncoder(boolean staticc, boolean superr){
		return add(new NBTClassEncoder(false, false, staticc, superr));
	}

	public MBT build(){
		return new MBT(encoders);
	}

}
