package aic.data;

import aic.data.dto.Tweet;

public interface ITweetWriter {
	public void write(Tweet tweet);
	public void index();
	public void close();
}
