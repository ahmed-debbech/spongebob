## for dev
.phony: st
st:
		docker compose down && docker compose up --build
