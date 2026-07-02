FROM python:3.7-buster 

ARG BUILD_ENV
ARG OVERCOOKED_BRANCH
ARG GRAPHICS

WORKDIR /app

# Install non-chai dependencies
COPY ./requirements.txt ./requirements.txt
RUN pip install -r requirements.txt

# Install eventlet production server if production build
RUN if [ "$BUILD_ENV" = "production" ] ; then pip install eventlet ; fi

# Clone chai code
RUN git clone --recursive https://github.com/margaritaradeva/OvercookedAIGameLogic.git --branch $OVERCOOKED_BRANCH --single-branch /OvercookedAIGameLogic

# Patch ray version for current platform/Python compatibility
RUN sed -i "s/ray\\[rllib\\]==2.0.0/ray\\[rllib\\]==2.2.0/g" /OvercookedAIGameLogic/setup.py

# Optional: print patched dependency so build log confirms the change
RUN grep -R "ray\[rllib\]" -n /OvercookedAIGameLogic/setup.py || true

# Dummy data_dir so things don't break
RUN echo "import os; DATA_DIR=os.path.abspath('.')" >>/OvercookedAIGameLogic/src/human_aware_rl/data_dir.py

# Install chai dependencies
RUN pip install -e '/OvercookedAIGameLogic[harl]'

RUN sed -i 's|http://deb.debian.org/debian|http://archive.debian.org/debian|g' /etc/apt/sources.list && \
    sed -i 's|http://security.debian.org/debian-security|http://archive.debian.org/debian-security|g' /etc/apt/sources.list && \
    sed -i '/buster-updates/d' /etc/apt/sources.list && \
    apt-get -o Acquire::Check-Valid-Until=false update && \
    apt-get install -y libgl1-mesa-dev && \
    rm -rf /var/lib/apt/lists/*

# Copy over remaining files
COPY ./static ./static
COPY ./*.py ./
COPY ./graphics/$GRAPHICS ./static/js/graphics.js
COPY ./config.json ./config.json



# Set environment variables that will be used by app.py
ENV HOST 0.0.0.0
ENV PORT 5000
ENV CONF_PATH config.json

# Do the thing
EXPOSE 5000
CMD ["python", "-u", "app.py"]
