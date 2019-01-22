package com.ibeifeng.hadoop.senior.io;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.WritableComparable;

public class PairWritable implements WritableComparable<PairWritable> {

	private int id;
	private String name;

	public PairWritable() {
	}

	public PairWritable(int id, String name) {
		this.set(id, name);
	}

	public void set(int id, String name) {
		this.setId(id);
		this.setName(getName());
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return id + "\t" + name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PairWritable other = (PairWritable) obj;
		if (id != other.id)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	public void write(DataOutput out) throws IOException {
		out.writeInt(id);
		out.writeUTF(name);

	}

	public void readFields(DataInput in) throws IOException {
		this.id = in.readInt();
		this.name = in.readUTF();
	}

	public int compareTo(PairWritable o) {

		int comp = Integer.valueOf(this.getId()).compareTo(
				Integer.valueOf(o.getId()));

		if (comp != 0) {
			return comp;
		}
		return this.getName().compareTo(o.getName());
	}

}
