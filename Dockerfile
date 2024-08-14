FROM gradle:jdk21 as base
COPY . .
RUN gradle assemble

RUN pwd; ls -l build/libs

FROM gradle:jdk21
# copy jar from build image
COPY --from=base /home/gradle/build/libs/lineage-0.0.1.jar /app/lineage.jar

ENTRYPOINT ["java"]
CMD ["-jar", "/app/lineage.jar"]
