FROM docker.io/nginx:1.27

EXPOSE 8080
EXPOSE 18080

ADD nginx.conf /etc/nginx/

# Main website
ADD site/target/ /var/www/html/site
# Docs site
ADD docs/public/ /var/www/html/docs
# Monkey projects static site
ADD monkey-projects/assets/ /var/www/html/monkey-projects
