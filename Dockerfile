FROM docker.io/nginx:1.25

EXPOSE 8080
EXPOSE 18080

ADD nginx.conf /etc/nginx/

ADD public/ /var/www/html/
