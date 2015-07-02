[![Travis](http://img.shields.io/travis/csgf/scoap3-harvester-api/master.png)](https://travis-ci.org/csgf/scoap3-harvester-api)
[![Documentation Status](https://readthedocs.org/projects/csgf/badge/?version=latest)](http://csgf.readthedocs.org)
[![License](https://img.shields.io/github/license/csgf/scoap3-harvester-api.svg?style?flat)](http://www.apache.org/licenses/LICENSE-2.0.txt)


#scoap3-harvester-api



/** The MIT License (MIT)
*
* Copyright (c) 2014 INFN Division of Catania
*
* Permission is hereby granted, free of charge, to any person obtaining a copy
* of this software and associated documentation files (the "Software"), to deal
* in the Software without restriction, including without limitation the rights
* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:
* The above copyright notice and this permission notice shall be included in
* all copies or substantial portions of the Software.
*
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
* THE SOFTWARE.
*/

The project aims to do harvesting of the SCOAP3 resources ,using API KEY, and insert them into Open Access Repository(http://www.openaccessrepository.it)


There are 3 classes:
- Scoap3withAPI.java is the main class. 
 	It’s mandatory to put  your private and public key to do a query.(String privateKey and      String 	publicKey)
	Using the startDate parameter, you can find all records created from start date(String startDate)

-Scoap3_step2. java is a class to separate the INFN resources from OTHERS.

-HmacSha1Signature. java is a class to generate the signature from private key.

The script returns a folder that contains :
-INFN folder (all INFN resources in MARCXML format )
-OTHER folder ((all OTHER resources in MARCXML format )
