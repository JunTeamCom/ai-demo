curl http://localhost:8080/web/ask \
  -X POST \
  -H "Content-Type: application/json" \
  -H "X_CONVERSATION_ID: C-2026-001" \
  -d '{"title": "德国", "question": "购物一般有哪些渠道？"}' 
