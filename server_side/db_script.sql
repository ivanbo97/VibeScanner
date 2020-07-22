CREATE TABLE `trk` (
  `id` int(11) NOT NULL,
  `creator` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `trackpt` (
  `id` int(11) NOT NULL,
  `trackid` int(11) NOT NULL,
  `lat` DOUBLE NOT NULL,
  `lon` DOUBLE NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `acceleration` (
  `id` int(11) NOT NULL,
  `trackptid` int(11) NOT NULL,
  `xAccel` DOUBLE NOT NULL,
  `yAccel` DOUBLE NOT NULL,
  `zAccel` DOUBLE NOT NULL 	
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `rotation` (
  `id` int(11) NOT NULL,
  `trackptid` int(11) NOT NULL,
  `xOrient` DOUBLE NOT NULL,
  `yOrient` DOUBLE NOT NULL,
  `zOrient` DOUBLE NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

ALTER TABLE `trackpt`
  ADD PRIMARY KEY (`id`);
ALTER TABLE `acceleration`
  ADD PRIMARY KEY (`id`);
ALTER TABLE `rotation`
  ADD PRIMARY KEY (`id`);
ALTER TABLE `trk`
  ADD PRIMARY KEY (`id`);
ALTER TABLE `trk`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=1;
ALTER TABLE `trackpt`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=1;
ALTER TABLE `acceleration`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=1;
ALTER TABLE `rotation`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=1;


