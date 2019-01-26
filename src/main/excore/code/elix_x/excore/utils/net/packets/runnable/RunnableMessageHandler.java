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
package code.elix_x.excore.utils.net.packets.runnable;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.function.Function;

import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public abstract class RunnableMessageHandler<REQ extends IMessage, REPLY extends IMessage> implements IMessageHandler<REQ, REPLY> {

	public Function<Pair<REQ, MessageContext>, Pair<Runnable, REPLY>> run;

	public RunnableMessageHandler(Function<Pair<REQ, MessageContext>, Pair<Runnable, REPLY>> run){
		this.run = run;
	}

	@Override
	public REPLY onMessage(REQ message, MessageContext ctx){
		Pair<Runnable, REPLY> pair = run.apply(new ImmutablePair<REQ, MessageContext>(message, ctx));
		getThreadListener(ctx).addScheduledTask(pair.getKey());
		return pair.getValue();
	}

	public abstract IThreadListener getThreadListener(MessageContext ctx);

}
