FROM ubuntu:14.04
ADD etc etc
ADD opt opt
ADD mnt mnt
ENV PATH  "$PATH:/opt/bin"
ENV LD_LIBRARY_PATH "$LD_LIBRARY_PATH:/opt/libs/hdf5-1.8.13/lib:/opt/libs/zlib1.2.8/lib:/opt/libs/netcdf.4.3.2/lib"
RUN DEBIAN_FRONTEND=noninteractive apt-get update 
RUN DEBIAN_FRONTEND=noninteractive etc/opt/install-ioapi && etc/opt/install-smoke
RUN DEBIAN_FRONTEND=noninteractive apt-get install -y openmpi-bin libopenmpi-dev
RUN DEBIAN_FRONTEND=noninteractive etc/opt/install-camx
RUN DEBIAN_FRONTEND=noninteractive apt-get install -y openssh-server nano python3 openjdk-11-jdk nco
RUN DEBIAN_FRONTEND=noninteractive etc/opt/build-preprocessors.sh
ENTRYPOINT ["java", "/opt/bin/camxRunner.jar"]
CMD ["20150101", "48", "Thessaloniki","8","/opt/data/wrfoutput-Thesaloniki","/opt/data/emiss-inv-Thessaloniki","http://vasilis.pw","1"]
