FROM docker.io/alpine:latest AS base

# Common assets site
ADD assets/ /var/www/html/assets
# Download bootstrap icons, and exctract some files from it
RUN wget -O /tmp/bootstrap-icons.zip https://github.com/twbs/icons/releases/download/v1.11.3/bootstrap-icons-1.11.3.zip
RUN cd /tmp\
    && unzip -q bootstrap-icons.zip\
    && rm bootstrap-icons.zip
RUN cd /tmp/bootstrap* \
    && cp font/bootstrap-icons.min.css /var/www/html/assets/css/ \
    && cp -R font/fonts /var/www/html/assets/css/

# Main website
ADD site/target/ /var/www/html/site
RUN ln -s /var/www/html/site /var/www/html/www
# Docs site
ADD docs/target/site/ /var/www/html/docs
# Monkey projects static site
ADD monkey-projects/assets/ /var/www/html/monkey-projects
# Error pages
ADD site/target/error-404.html /var/www/html/

FROM docker.io/nginx:1.27

EXPOSE 8080
EXPOSE 18080

ADD nginx.conf /etc/nginx/

COPY --from=base /var/www/html /var/www/html
