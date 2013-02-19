/**
 * Copyright (C) 2010-2013 Axel Morgner, structr <structr@structr.org>
 *
 * This file is part of structr <http://structr.org>.
 *
 * structr is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * structr is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with structr.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.structr.websocket.command.dom;

import java.util.Map;
import org.structr.web.entity.dom.DOMNode;
import org.structr.websocket.StructrWebSocket;
import org.structr.websocket.command.AbstractCommand;
import org.structr.websocket.message.MessageBuilder;
import org.structr.websocket.message.WebSocketMessage;
import org.w3c.dom.DOMException;

/**
 *
 * @author Christian Morgner
 */
public class InsertBeforeDOMNodeCommand extends AbstractCommand {

	static {
		
		StructrWebSocket.addCommand(InsertBeforeDOMNodeCommand.class);
	}
	
	@Override
	public void processMessage(WebSocketMessage webSocketData) {

		Map<String, Object> nodeData = webSocketData.getNodeData();
		String parentId              = (String) nodeData.get("parentId");
		String newId                 = (String) nodeData.get("newId");
		String refId                 = (String) nodeData.get("refId");
		String pageId                = webSocketData.getPageId();
		
		if (pageId != null) {

			// check for parent ID before creating any nodes
			if (parentId == null) {
		
				getWebSocket().send(MessageBuilder.status().code(422).message("Cannot replace node without parentId").build(), true);		
				return;
			}

			// check if parent node with given ID exists
			DOMNode parentNode = getDOMNode(parentId);
			if (parentNode == null) {
		
				getWebSocket().send(MessageBuilder.status().code(404).message("Parent node not found").build(), true);		
				return;
			}

			// check for old ID before creating any nodes
			if (refId == null) {
		
				getWebSocket().send(MessageBuilder.status().code(422).message("Cannot insert node without refId").build(), true);
				return;
			}

			// check if old node with given ID exists
			DOMNode refNode = getDOMNode(refId);
			if (refNode == null) {
		
				getWebSocket().send(MessageBuilder.status().code(404).message("Reference node not found").build(), true);		
				return;
			}

			// check for new ID before creating any nodes
			if (newId == null) {
		
				getWebSocket().send(MessageBuilder.status().code(422).message("Cannot replace node without newId").build(), true);
				return;
			}

			// check if new node with given ID exists
			DOMNode newNode = getDOMNode(newId);
			if (newNode == null) {
		
				getWebSocket().send(MessageBuilder.status().code(404).message("New node not found").build(), true);		
				return;
			}
			
			try {
				parentNode.insertBefore(newNode, refNode);

			} catch (DOMException dex) {

				// send DOM exception
				getWebSocket().send(MessageBuilder.status().code(422).message(dex.getMessage()).build(), true);		
			}
			
		} else {
		
			getWebSocket().send(MessageBuilder.status().code(422).message("Cannot insert node without pageId").build(), true);		
		}
	}

	@Override
	public String getCommand() {
		return "INSERT_BEFORE_DOM_NODE";
	}

}