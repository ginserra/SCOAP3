*********************
SCOAP3 HARVESTER API 
*********************
============
About
============


 

.. line-block:: The project aims to do harvesting of the `SCOAP3 <http://scoap3.org/>`_ resources ,using API KEY, and insert them into `Open Access Repository <http://www.openaccessrepository.it>`_


============
Usage
============

The project contains 3 classes:

- .. line-block:: *Scoap3withAPI.java* is the main class. It’s mandatory to put  your private and public key to do a query (String privateKey and StringpublicKey). Using the startDate parameter, you can find all records created from start date (String startDate)



- *Scoap3_step2.java* is a class to separate the INFN resources from OTHERS.
- *HmacSha1Signature.java* is a class to generate the signature from private key.

The script returns a folder that contains :

- INFN folder (all INFN resources in MARCXML format )
- OTHER folder (all OTHER resources in MARCXML format )




=============
Contributors
=============
Please feel free to contact us any time if you have any questions or comments.

.. _INFN: http://www.ct.infn.it/

:Authors:

 `Rita RICCERI <mailto:rita.ricceri@ct.infn.it>`_ - Italian National Institute of Nuclear Physics (INFN_),

 `Giuseppina INSERRA <mailto:giuseppina.inserra@ct.infn.it>`_ - Italian National Institute of Nuclear Physics (INFN_), 

 `Carla CARRUBBA <mailto:carla.carrubba@ct.infn.it>`_ - Italian National Institute of Nuclear Physics (INFN_)
 

