FROM docker.io/nginx:1.25

EXPOSE 8080
EXPOSE 18080

ADD nginx.conf /etc/nginx/

# Main website
ADD site/target/ /var/www/html/site
# Docs site
ADD docs-contents/public/blog/ /var/www/html/docs
