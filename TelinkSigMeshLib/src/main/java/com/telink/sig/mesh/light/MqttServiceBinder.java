package com.telink.sig.mesh.light;

import android.os.Binder;

class MeshServiceBinder extends Binder {

	private MeshService mMeshService;
	private String activityToken;

	MeshServiceBinder(MeshService mMeshService) {
		this.mMeshService = mMeshService;
	}

	/**
	 * @return a reference to the Service
	 */
	public MeshService getService() {
		return mMeshService;
	}

	void setActivityToken(String activityToken) {
		this.activityToken = activityToken;
	}

	/**
	 * @return the activityToken provided when the Service was started
	 */
	public String getActivityToken() {
		return activityToken;
	}

}