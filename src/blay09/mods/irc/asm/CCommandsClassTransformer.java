// Copyright (c) 2013, Christopher "blay09" Baker
// All rights reserved.

package blay09.mods.irc.asm;

import java.util.Iterator;

import net.minecraft.launchwrapper.IClassTransformer;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FrameNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public class CCommandsClassTransformer implements IClassTransformer {

	@Override
	public byte[] transform(String name, String transformedName, byte[] bytes) {
		// EntityClientPlayerMP
		if(name.equals("bdf")) {
			System.out.println("********* INSIDE OBFUSCATED ENTITYCLIENTPLAYERMP TRANSFORMER ABOUT TO PATCH: " + name);
			return patchClassASM("EntityClientPlayerMP", name, bytes, true);
		}
		if(name.equals("net.minecraft.client.entity.EntityClientPlayerMP")) {
			System.out.println("********* INSIDE ENTITYCLIENTPLAYERMP TRANSFORMER ABOUT TO PATCH: " + name);
			return patchClassASM("EntityClientPlayerMP", name, bytes, false);
		}
		// GuiChat
		if(name.equals("aut")) {
			System.out.println("********* INSIDE OBFUSCATED GUICHAT TRANSFORMER ABOUT TO PATCH: " + name);
			return patchClassASM("GuiChat", name, bytes, true);
		}
		if(name.equals("net.minecraft.client.gui.GuiChat")) {
			System.out.println("********* INSIDE GUICHAT TRANSFORMER ABOUT TO PATCH: " + name);
			return patchClassASM("GuiChat", name, bytes, false);
		}
		return bytes;
	}
	
	private byte[] patchClassASM(String cname, String name, byte[] bytes, boolean obfuscated) {
		String targetMethodName = null;
		String targetMethodDesc = null;
		if(cname.equals("EntityClientPlayerMP")) {
			targetMethodName = obfuscated ? "b" : "sendChatMessage";
			targetMethodDesc = "(Ljava/lang/String;)V";
		} else if(cname.equals("GuiChat")) {
			targetMethodName = obfuscated ? "a" : "drawScreen";
			targetMethodDesc = "(IIF)V";
		}
		
		ClassNode classNode = new ClassNode();
		ClassReader classReader = new ClassReader(bytes);
		classReader.accept(classNode, 0);
		
		Iterator<MethodNode> methods = classNode.methods.iterator();
		while(methods.hasNext()) {
			MethodNode m = methods.next();
			
			if(m.name.equals(targetMethodName) && m.desc.equals(targetMethodDesc)) {
				System.out.println("********* INSIDE TARGET METHOD");
				
				AbstractInsnNode currentNode = null;
				AbstractInsnNode targetNode = null;
				
				Iterator<AbstractInsnNode> iter = m.instructions.iterator();
				while(iter.hasNext()) {
					currentNode = iter.next();
					
					if(currentNode instanceof LineNumberNode) {
						targetNode = currentNode;
						break;
					}
				}
				
				InsnList toInject = new InsnList();
				if(cname.equals("EntityClientPlayerMP")) {
					toInject.add(new VarInsnNode(Opcodes.ALOAD, 0));
					toInject.add(new VarInsnNode(Opcodes.ALOAD, 1));
					toInject.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "blay09/mods/irc/client/ClientChatHandler", "handleClientChat", "(Ljava/lang/String;)Z"));
					LabelNode l1 = new LabelNode();
					toInject.add(new JumpInsnNode(Opcodes.IFEQ, l1));
					LabelNode l2 = new LabelNode();
					toInject.add(l2);
					toInject.add(new InsnNode(Opcodes.RETURN));
					toInject.add(l1);
					toInject.add(new FrameNode(Opcodes.F_SAME, 0, null, 0, null));
				} else if(cname.equals("GuiChat")) {
					toInject.add(new VarInsnNode(Opcodes.ALOAD, 0));
					toInject.add(new VarInsnNode(Opcodes.ILOAD, 1));
					toInject.add(new VarInsnNode(Opcodes.ILOAD, 2));
					toInject.add(new VarInsnNode(Opcodes.FLOAD, 3));
					toInject.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "blay09/mods/irc/client/GuiChatOverlay", "drawOverlay", "(IIF)V"));
				}
				m.instructions.insert(targetNode, toInject);
				System.out.println("********* PATCHING COMPLETE");
				break;
			}
		}
		ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
		classNode.accept(writer);
		return writer.toByteArray();
	}

	
}
