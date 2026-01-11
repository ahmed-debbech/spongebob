## for dev
.phony: st
st:
	docker compose down && docker compose up --build
clean: 
	yes | rm -r download_internal output redis_data scdownloads
