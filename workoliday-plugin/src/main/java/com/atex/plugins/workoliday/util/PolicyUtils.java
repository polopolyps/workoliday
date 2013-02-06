package com.atex.plugins.workoliday.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.polopoly.cm.ContentId;
import com.polopoly.cm.ContentReference;
import com.polopoly.cm.ExternalContentId;
import com.polopoly.cm.app.policy.CheckboxPolicy;
import com.polopoly.cm.app.policy.ContentSingleSelectPolicy;
import com.polopoly.cm.app.policy.DateTimePolicy;
import com.polopoly.cm.app.policy.RadioButtonPolicy;
import com.polopoly.cm.app.policy.SelectableSubFieldPolicy;
import com.polopoly.cm.app.policy.SingleValued;
import com.polopoly.cm.client.CMException;
import com.polopoly.cm.collections.ContentList;
import com.polopoly.cm.policy.Policy;
import com.polopoly.cm.policy.PolicyCMServer;
import com.polopoly.cm.policy.PolicyImplBase;
import com.polopoly.util.StringUtil;

/**
 * Utility class for accessing child policies without having to write too much
 * code.
 * 
 */
public class PolicyUtils {

    private static final String CLASS = PolicyUtils.class.getName();
    private static Logger logger = Logger.getLogger(CLASS);

    private PolicyUtils() {}
    
    /**
     * Utility method to get a single value
     * 
     * @param policy
     *            the policy to get the list from
     * @param names
     *            names of the components
     * @return String or null if none found or Exception caught
     */
    public static String getSingleValue(Policy policy, String... names) {
        String ret = null;
        try {
            for (String n : names) {
                policy = policy.getChildPolicy(n);
            }
            if (policy != null) {
                ret = ((SingleValued) policy).getValue();
            }
        } catch (Exception e) {
            logger.log(Level.FINEST, "Error getting single value.", e);
        }
        return ret;
    }

    /**
     * Utility method to get a single value
     * 
     * @param policy
     *            the policy to get the list from
     * @param names
     *            names of the components
     * @return String or defaultValue if none found or Exception caught
     */
    public static String getSingleValue(String defaultValue, Policy policy,
            String... names) {
        String value = getSingleValue(policy, names);
        if (StringUtil.isEmpty(value)) {
            value = defaultValue;
        }
        return value;
    }

    public static ContentId getSingleContentId(Policy policy, String name) {
        return getSingleContentId(policy, new String[] { name });
    }

    /**
     * Utility method to get a single ContentId
     * 
     * @param policy
     *            the policy to get the list from
     * @param names
     *            name of the list
     * @return ContentId or null if none found or Exception caught
     */
    public static ContentId getSingleContentId(Policy policy, String[] names) {
        ContentId ret = null;
        try {
            for (int i = 0; i < names.length; i++) {
                policy = policy.getChildPolicy(names[i]);
            }
            ret = ((ContentSingleSelectPolicy) policy).getReference();
        } catch (Exception e) {
            logger.log(Level.FINEST, "Error getting single ContentId.", e);
        }
        return ret;
    }

    public static Date getDateValue(Policy policy, String... names) {
        Date ret = null;
        try {
            for (String n : names) {
                policy = policy.getChildPolicy(n);
            }

            if (policy != null) {
                ret = ((DateTimePolicy) policy).getDate();
            }
        } catch (Exception e) {
            logger.log(Level.FINEST, "Error getting date value.", e);
        }
        return ret;
    }

    /**
     * Utility method to check if checkbox is checked
     * 
     * @param policy
     *            the policy to get the list from
     * @param names
     *            names of the components
     * @return true if checked or false if not checked, none found or Exception
     *         caught
     */
    public static boolean isChecked(Policy policy, String... names) {
        boolean ret = false;
        try {
            for (String n : names) {
                policy = policy.getChildPolicy(n);
            }
            ret = ((CheckboxPolicy) policy).getChecked();
        } catch (Exception e) {
            logger.log(Level.FINEST, "Error getting isChecked.", e);
        }
        return ret;
    }
    
    /**
     * Utility method to check if checkbox is checked with default value
     * 
     * @param defaultValue value of the boolean if no other found.
     * @param policy the policy to get the list from
     * @param names names of the components
     * @return true if checked or false if not checked, none found or Exception caught
     */
    public static boolean isChecked(boolean defaultValue, Policy policy, String... names) {
        boolean ret = defaultValue;
        try {
            for (String n : names) {
                policy = policy.getChildPolicy(n);
            }
            ret = ((CheckboxPolicy) policy).getChecked();
        } catch (Exception e) {
            logger.log(Level.FINEST, "Error getting isChecked.", e);
        }
        return ret;
    }

    /**
     * Method to get a Policy object by it's ContentId.
     * 
     * @param contentId
     *            the policy's contentId
     * @param cmServer
     *            the PolicyCMServer
     * @return Policy object or null if not found
     */
    public static Policy getPolicy(ContentId contentId, PolicyCMServer cmServer) {
        Policy policy = null;
        if (contentId != null) {
            try {
                policy = cmServer.getPolicy(contentId);
            } catch (Exception e) {
                logger.log(Level.FINEST, "Error getting policy for contentId: "
                        + contentId, e);
            }
        }
        return policy;
    }

    /**
     * Utility method to get a single Policy in the provided policy's content
     * list.
     * 
     * @param policy
     *            the policy to get the list from
     * @param name
     *            name of the list
     * @return Policy or null if none found or Exception caught
     */
    public static Policy getSinglePolicyFromList(PolicyImplBase policy,
            String name) {
        Policy p = null;
        try {
            ContentList cl = policy.getContentList(name);
            if (cl != null && cl.size() > 0) {
                p = PolicyUtils.getPolicy(
                        cl.getEntry(0).getReferredContentId(), policy
                                .getCMServer());
            }
        } catch (Exception e) {
            logger.log(Level.FINEST, "Error getting single Policy from list.",
                    e);
        }
        return p;
    }

    /**
     * Utility method to get a list of Reference Meta Data policy objects in the
     * provided policy's content list.
     * 
     * @param policy
     *            the policy to get the list from
     * @param name
     *            name of the list
     * @return List of Reference Meta Data Policies
     */
    public static List<Policy> getRMDPoliciesByListName(PolicyImplBase policy,
            String name) {
        List<Policy> ret = new ArrayList<Policy>();

        try {
            ContentList cl = policy.getContentList(name);
            if (cl != null) {
                PolicyCMServer cmServer = policy.getCMServer();
                int size = cl.size();
                for (int i = 0; i < size; i++) {
                    ContentId referenceMetaDataId = cl.getEntry(i)
                            .getReferenceMetaDataId();
                    if (referenceMetaDataId != null) {
                        Policy rmd = PolicyUtils.getPolicy(referenceMetaDataId,
                                cmServer);
                        if (rmd != null) {
                            ret.add(rmd);
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.log(Level.FINEST,
                    "Error getting list of Reference Meta Data policys.", e);
        }
        return ret;
    }

    /**
     * Utility method to get the selected value from a SelectPolicy
     * 
     * @param policy
     *            the policy to get the options from
     * @param names
     *            names of the components
     * @return the selected option or null if nothing is selected or Exception
     *         caught
     */
    public static String getSelectedOption(Policy policy, String... names) {
        String ret = null;
        try {
            for (String n : names) {
                policy = policy.getChildPolicy(n);
            }
            ret = ((RadioButtonPolicy) policy).getValue();
        } catch (Exception e) {
            logger.log(Level.FINEST, "Error getting getSelected.", e);
        }
        return ret;
    }

    public static String getAsString(Policy p) {
        try {
            if (p.getParentPolicy() != null) {
                String name;
                try {
                    name = p.getPolicyName();
                } catch (CMException e) {
                    name = e.toString();
                }
                return name + " in "
                        + p.getContentId().getContentId().getContentIdString();
            } else {
                String contentIdString = null;
                ExternalContentId extId = p.getContent().getExternalId();
                if (extId != null)
                    contentIdString = extId.getExternalId();
                if (contentIdString == null) {
                    contentIdString = p.getContentId().getContentId()
                            .getContentIdString();
                }

                String contentName = p.getContent().getName();

                if (contentName == null || contentName.toString().equals("")) {
                    contentName = "unnamed "
                            + p.getInputTemplate().getExternalId()
                                    .getExternalId();
                }

                return contentName + " (" + contentIdString + ")";
            }
        } catch (CMException cmex) {

        }
        return "unnamed " + p.getClass().getName();
    }

    /**
     * Utility method to get a single RMD Policy in the provided policy's
     * content list.
     * 
     * @param policy
     *            the policy to get the list from
     * @param name
     *            name of the list
     * @return Policy or null if none found or Exception caught
     */
    public static Policy getSingleRMDPolicyFromList(PolicyImplBase policy,
            String name) {
        Policy p = null;
        try {
            ContentList cl = policy.getContentList(name);
            if (cl != null && cl.size() > 0) {
                ContentReference entry = cl.getEntry(0);

                if (entry.hasReferenceMetaData()) {
                    p = PolicyUtils.getPolicy(entry.getReferenceMetaDataId(),
                            policy.getCMServer());
                }
            }
        } catch (Exception e) {
            logger.log(Level.FINEST,
                    "Error getting single RMD Policy from list.", e);
        }
        return p;
    }

    /**
     * Utility method to get a single float value
     * 
     * @param policy
     *            the policy to get the list from
     * @param names
     *            names of the components
     * @return float or default if none found or Exception caught
     */

    public static float getSingleFloatValue(Policy policy, float defaultValue,
            String... names) {
        float value = defaultValue;
        String s = getSingleValue(String.valueOf(defaultValue), policy, names);
        if (!StringUtil.isEmpty(s)) {
            value = Float.parseFloat(s);
        }
        return value;
    }

    public static int getSingleIntValue(Policy policy, int defaultValue,
            String... names) {
        int value = defaultValue;
        String s = getSingleValue(String.valueOf(defaultValue), policy, names);
        if (!StringUtil.isEmpty(s)) {
            value = Integer.parseInt(s);
        }
        return value;
    }


    /**
     * Get the first contentId in a content list (not the reference meta data
     * id).
     * 
     * @param policy
     *            the policy that owns the list
     * @param listName
     *            the content list name to get the contentId from
     * @return first contentId in list
     */
    public static ContentId getFirstContentIdInList(PolicyImplBase policy,
            String listName) {
        ContentId ret = null;
        try {
            ContentList cl = policy.getContentList(listName);

            if (cl != null) {
                for (int i = 0; i < cl.size(); i++) {
                    ContentReference entry = cl.getEntry(i);
                    if (entry != null) {
                        ret = entry.getReferredContentId();
                    }
                }
            }
        } catch (Exception e) {
            logger.log(Level.FINEST,
                    "Error getting the contentId from the list " + listName
                            + " from policy " + policy.getContentId(), e);
        }
        return ret;
    }

    /**
     * Utility method to get a list of policy objects in the provided policy's
     * content list.
     * 
     * @param policy
     *            the policy to get the list from
     * @param name
     *            the name of the content list
     * @return List of Policy objects
     */
    public static List<Policy> getPoliciesByListName(PolicyImplBase policy,
            String name) {
        List<Policy> ret = new ArrayList<Policy>();
        try {
            ContentList cl = policy.getContentList(name);

            if (cl != null) {
                PolicyCMServer cmServer = policy.getCMServer();
                for (int i = 0; i < cl.size(); i++) {
                    ContentReference entry = cl.getEntry(i);
                    Policy p = PolicyUtils.getPolicy(entry
                            .getReferredContentId(), cmServer);
                    if (p != null) {
                        ret.add(p);
                    }
                }
            }
        } catch (Exception e) {
            logger.log(Level.FINEST, "Error getting list of policys.", e);
        }
        return ret;
    }

    
    /**
     * Checks selected sub field matching name in 'values'.
     * @param policy To check SelectableSubFieldPolicy for.
     * @param fieldName Name of the SelectedSubFieldPolicy field.
     * @param values Selected value to check for, if multiple first hit will return true.
     * @return True if selected sub field matches value.
     */
    public static boolean hasSelectableSubFieldSelected(Policy policy, String fieldName, String...values) {
        try {
            Policy selectableSubfield = policy.getChildPolicy(fieldName);
            if (selectableSubfield != null && selectableSubfield instanceof SelectableSubFieldPolicy) {
                String selectedSubfield = ((SelectableSubFieldPolicy)selectableSubfield).getSelectedSubFieldName();
                for (String value : values) {
                    if (value.equals(selectedSubfield)) {
                        return true;
                    }
                }
            }
        } catch (CMException e) {
            logger.log(Level.WARNING, "Unable to fetch values from policy with id: "
                    + policy.getContentId().getContentIdString(), e);
        }
        return false;
    }
}

