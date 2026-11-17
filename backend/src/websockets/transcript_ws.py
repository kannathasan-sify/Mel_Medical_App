from fastapi import APIRouter, WebSocket, WebSocketDisconnect
import json
import logging
from datetime import datetime, timezone

logger = logging.getLogger("MelWebSocket")
router = APIRouter(tags=["Real-time Transcription WebSocket"])

class ConnectionManager:
    def __init__(self):
        self.active_connections: dict[str, list[WebSocket]] = {}

    async def connect(self, consultation_id: str, websocket: WebSocket):
        await websocket.accept()
        if consultation_id not in self.active_connections:
            self.active_connections[consultation_id] = []
        self.active_connections[consultation_id].append(websocket)
        logger.info(f"WebSocket client connected to consultation {consultation_id}")

    def disconnect(self, consultation_id: str, websocket: WebSocket):
        if consultation_id in self.active_connections:
            self.active_connections[consultation_id].remove(websocket)
            if not self.active_connections[consultation_id]:
                del self.active_connections[consultation_id]
        logger.info(f"WebSocket client disconnected from consultation {consultation_id}")

    async def broadcast_segment(self, consultation_id: str, message: dict):
        if consultation_id in self.active_connections:
            for connection in self.active_connections[consultation_id]:
                await connection.send_text(json.dumps(message))

manager = ConnectionManager()

@router.websocket("/ws/v1/consultations/{consultation_id}/transcript")
async def websocket_transcript_endpoint(websocket: WebSocket, consultation_id: str):
    """
    WebSocket endpoint for real-time live transcription streaming.
    Receives audio chunks or partial text events from mobile client,
    and broadcasts timestamped, speaker-diarized transcript segments.
    """
    await manager.connect(consultation_id, websocket)
    try:
        # Send initial connection confirmation
        await websocket.send_text(json.dumps({
            "type": "connection_established",
            "consultation_id": consultation_id,
            "timestamp": datetime.now(timezone.utc).isoformat()
        }))

        while True:
            data = await websocket.receive_text()
            try:
                payload = json.loads(data)
                event_type = payload.get("type", "audio_chunk")
                
                if event_type == "partial_transcript":
                    # Broadcast partial transcript segment back to listeners
                    response_event = {
                        "type": "transcript_segment",
                        "consultation_id": consultation_id,
                        "speaker_role": payload.get("speaker_role", "UNKNOWN"),
                        "text": payload.get("text", ""),
                        "start_time_ms": payload.get("start_time_ms", 0),
                        "end_time_ms": payload.get("end_time_ms", 0),
                        "is_final": payload.get("is_final", False)
                    }
                    await manager.broadcast_segment(consultation_id, response_event)
                elif event_type == "ping":
                    await websocket.send_text(json.dumps({"type": "pong"}))
            except json.JSONDecodeError:
                pass
    except WebSocketDisconnect:
        manager.disconnect(consultation_id, websocket)

