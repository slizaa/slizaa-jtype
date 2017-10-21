#! /bin/bash

ftp_ip=ftp.wuetherich.com
target_dir=slizaa-scanner-jtype

echo TRAVIS_BUILD_DIR: $TRAVIS_BUILD_DIR

PRODUCTS_DIR=$TRAVIS_BUILD_DIR/slizaa-scanner-jtype-eclipse/org.slizaa.scanner.jtype.eclipse.repository/target/repository
echo PRODUCTS_DIR: $PRODUCTS_DIR

filesToUpload=$(find $PRODUCTS_DIR -name "*.jar" -printf '%P\n')
echo filesToUpload: $filesToUpload

# change the working directory
cd $PRODUCTS_DIR

# upload all files
for localFile in $filesToUpload;
do
  echo "Uploading $localFile"
  curl -T $localFile ftp://"$user":"$password"@"$ftp_ip/$target_dir/$localFile"
done